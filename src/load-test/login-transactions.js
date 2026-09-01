import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import { Trend } from 'k6/metrics';

const URL = __ENV.K6_URL || 'localhost:8080';
const BASE_URL = `http://${URL}/api`;

const PIX_KEYS = JSON.parse(open('../main/resources/dict/simulated-external-keys.json')).map(i => i.pixKey);

const USERS = new SharedArray('users', function () {
    return JSON.parse(open('./users_accounts.json'));
});

const ALL_DESTINATIONS = USERS.map(user => ({
    originAccount: user.account,
    type: user.type,
    agencyNumber: user.agency
}));

function getRandomAmount(min = 10, max = 500) {
    return parseFloat((Math.random() * (max - min) + min).toFixed(2));
}

function getRandomElement(array) {
    return array[Math.floor(Math.random() * array.length)];
}

const loginDuration = new Trend('login_duration');
const depositDuration = new Trend('deposit_duration');
const internalDuration = new Trend('internal_duration');
const externalDuration = new Trend('external_duration');
const pixDuration = new Trend('pix_duration');
const withdrawDuration = new Trend('withdraw_duration');
const balanceDuration = new Trend('balance_duration');
const statementDuration = new Trend('statement_duration');

export const options = {
    scenarios: {
        concurrent_users: {
            executor: 'constant-vus',
            vus: USERS.length,
            duration: '30s',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<600'],
    },
};

export default function () {

    const user = USERS[(__VU - 1) % USERS.length];

    const loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
        cpf: user.cpf,
        password: user.password,
    }), {headers: { 'Content-Type': 'application/json' }});

    loginDuration.add(loginRes.timings.duration);

    const loginOk = check(loginRes, {
        'login: status 200': (r) => r.status === 200,
        'login: token presente': (r) => !!r.json('token'),
    });

    if (!loginOk) {
        console.error(`VU ${__VU} (CPF: ${user.cpf}) falhou no login: ${loginRes.status}`);
        return;
    }

    const authHeaders = {
        headers: {'Content-Type': 'application/json', 'Authorization': `Bearer ${loginRes.json('token')}`}
    };

    sleep(2);

    const resDeposit = http.post(`${BASE_URL}/account/${user.account}/transaction/deposit`, JSON.stringify({
        amount: getRandomAmount(),
        description: ''
    }), authHeaders);

    depositDuration.add(resDeposit.timings.duration);
    check(resDeposit, { 'deposito inicial ok': (r) => r.status === 200 || r.status === 201 });

    const ACTIONS = ['internal', 'external', 'pix', 'withdraw'];
    const availableActions = user.type === 'POUPANCA' ? ['pix', 'withdraw'] : ACTIONS;

    const randomAction = getRandomElement(availableActions);

    sleep(1);
    switch (randomAction) {

        case 'internal': {
            const possibleDestinations = ALL_DESTINATIONS.filter(acc => acc.originAccount !== user.account);
            const randomDestination = getRandomElement(possibleDestinations);

            const resInternal = http.post(`${BASE_URL}/account/${user.account}/transaction/internal`, JSON.stringify({
                destinationAccountNumber: randomDestination.originAccount,
                agencyNumber: randomDestination.agencyNumber,
                amount: getRandomAmount(),
                description: ''
            }), authHeaders);

            internalDuration.add(resInternal.timings.duration);
            check(resInternal, { 'transferencia interna ok': (r) => [200, 201].includes(r.status) });

            sleep(1);
            break;
        }

        case 'withdraw': {
            const resWithdraw = http.post(`${BASE_URL}/account/${user.account}/transaction/withdraw`, JSON.stringify({
                amount: getRandomAmount(),
                description: ''
            }), authHeaders);

            withdrawDuration.add(resWithdraw.timings.duration);
            check(resWithdraw, { 'saque ok': (r) => [200].includes(r.status) });

            const resBalance = http.get(`${BASE_URL}/account/${user.account}/balance`, authHeaders);

            balanceDuration.add(resBalance.timings.duration);
            check(resBalance, { 'saldo ok': (r) => r.status === 200 });

            sleep(1);
            break;
        }

        case 'external': {
            const resExternal = http.post(`${BASE_URL}/account/${user.account}/transaction/external`, JSON.stringify({
                destinationName: 'Tech Solucoes',
                destinationAccount: '524920166',
                destinationAgency: '0001',
                compe: '260',
                destinationDocument: '11.222.333/0001-81',
                amount: getRandomAmount(),
                description: ''
            }), authHeaders);

            externalDuration.add(resExternal.timings.duration);
            check(resExternal, { 'transferencia externa ok': (r) => [200, 202].includes(r.status) });

            sleep(1);
            break;
        }

        case 'pix': {
            const randomPix = getRandomElement(PIX_KEYS);

            const resPix = http.post(`${BASE_URL}/account/${user.account}/transaction/pix`, JSON.stringify({
                key: randomPix,
                amount: getRandomAmount(),
                description: ''
            }), authHeaders);

            pixDuration.add(resPix.timings.duration);
            check(resPix, { 'pix ok': (r) => [200, 202].includes(r.status) });

            const resStatement = http.get(`${BASE_URL}/account/${user.account}/statement`, authHeaders);
            statementDuration.add(resStatement.timings.duration);
            check(resStatement, { 'extrato ok': (r) => r.status === 200 });

            sleep(1);
            break;
        }
    }
    sleep(2);
}