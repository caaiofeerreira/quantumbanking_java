import http from 'k6/http';
import { check, sleep } from 'k6';

const URL = __ENV.URL || 'localhost:8080';
const BASE_URL = `http://${URL}/api`;

const users = [
    { cpf: '52998224725', password: '123456789', originAccount: '639502571', agencyNumber: '0001'},
    { cpf: '56607934870', password: '123456789', originAccount: '377156574', agencyNumber: '0001'},
    { cpf: '11144477735', password: '123456789', originAccount: '510323111', agencyNumber: '0001'},
    { cpf: '39053344705', password: '123456789', originAccount: '305838890', agencyNumber: '0001'},
    { cpf: '16899523000', password: '123456789', originAccount: '871116553', agencyNumber: '0001'},
    { cpf: '73013811820', password: '123456789', originAccount: '076987256', agencyNumber: '0002'},
    { cpf: '67083136858', password: '123456789', originAccount: '295130148', agencyNumber: '0002'}
];

const allAccounts = users.map(user => user.originAccount);

function getRandomAmount(min = 10, max = 200) {
    const randomAmount = Math.random() * (max - min) + min;
    return parseFloat(randomAmount.toFixed(2));
}

export const options = {
    scenarios: {
        concurrent_users: {
            executor: 'constant-vus',
            vus: users.length,
            duration: '30s'
        }
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<600']
    }
};

export default function() {

    const user = users[(__VU - 1) % users.length];

    const loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
        cpf: user.cpf,
        password: user.password,
    }), { headers: {'Content-Type': 'application/json'}});

    const loginOk = check(loginRes, {
        'login: status 200': (r) => r.status === 200,
        'login: token presente': (r) => !!r.json('token'),
    });

    if (!loginOk) {
        console.error(`VU ${__VU} (CPF: ${user.cpf}) falhou no login: ${loginRes.status}`);
        return;
    }

    const token = loginRes.json('token');
    const authHeaders = {
        headers: {'Content-Type': 'application/json', Authorization: `Bearer ${token}`}
    };

    sleep(1);

    const possibleDestinations = users.filter(acc => acc.originAccount !== user.originAccount);

    const randomUserDestination = possibleDestinations[Math.floor(Math.random() * possibleDestinations.length)];
    const randomDestinationAccount = randomUserDestination.originAccount;
    const destinationAgency = randomUserDestination.agencyNumber;

    const actions = ['internal', 'external', 'pix', 'withdraw'];
    const randomAction = actions[Math.floor(Math.random() * actions.length)];

    const res_deposit = http.post(`${BASE_URL}/account/${user.originAccount}/transaction/deposit`, JSON.stringify({
        amount: getRandomAmount(500, 1500),
        description: 'Deposito automatico para carga'
    }), authHeaders);

    check(res_deposit, {'deposito inicial ok': (r) => r.status === 200 || r.status === 202});
    sleep(1);

    if (randomAction === 'internal') {
        const res_internal = http.post(`${BASE_URL}/account/${user.originAccount}/transaction/internal`, JSON.stringify({
            destinationAccountNumber: randomDestinationAccount,
            agencyNumber: destinationAgency,
            amount: getRandomAmount(10, 100),
            description: 'Teste interno'
        }), authHeaders);

        check(res_internal, {
            'transferencia interna ok': (r) => r.status === 200 || r.status === 202 || r.status === 400 || r.status === 422
        });

        sleep(2);
        const res_balance = http.get(`${BASE_URL}/account/${user.originAccount}/balance`, authHeaders);
        check(res_balance, {'saldo ok': (r) => r.status === 200 });

    } else if (randomAction === 'withdraw') {
        const res_withdraw = http.post(`${BASE_URL}/account/${user.originAccount}/transaction/withdraw`, JSON.stringify({
            amount: getRandomAmount(5, 50),
            description: 'Saque teste'
        }), authHeaders);

        check(res_withdraw, {
            'saque ok': (r) => r.status === 200 || r.status === 202 || r.status === 400 || r.status === 422
        });

        sleep(2);

    } else if (randomAction === 'external') {
        const res_external = http.post(`${BASE_URL}/account/${user.originAccount}/transaction/external`, JSON.stringify({
            destinationName: 'Tech Solucoes',
            destinationAccount: '524920166',
            destinationAgency: '0001',
            compe: '260',
            destinationDocument: '11.222.333/0001-81',
            amount: getRandomAmount(10, 80),
            description: 'Externa'
        }), authHeaders);

        check(res_external, {
            'transferencia externa ok': (r) => r.status === 200 || r.status === 202 || r.status === 400 || r.status === 422
        });

        sleep(2);

    } else if (randomAction === 'pix') {
        const res_pix = http.post(`${BASE_URL}/account/${user.originAccount}/transaction/pix`, JSON.stringify({
            key: '11987654321',
            amount: getRandomAmount(10, 60),
            description: 'PIX teste'
        }), authHeaders);

        check(res_pix, {
            'pix ok': (r) => r.status === 200 || r.status === 202 || r.status === 400 || r.status === 422
        });

        sleep(2);
        const res_statement = http.get(`${BASE_URL}/account/${user.originAccount}/statement`, authHeaders);
        check(res_statement, {'extrato ok': (r) => r.status === 200 });
    }

    sleep(2);
}