function Request-Api {
    param (
        [Parameter(Mandatory=$true)][string]$Path,
        [Parameter(Mandatory=$false)][string]$Method = "GET",
        [Parameter(Mandatory=$false)]$Body = $null
    )

    $params = @{
        Uri         = "$env:BASE_URL$Path"
        Method      = $Method
        ContentType = "application/json"
        ErrorAction = "Stop"
    }

    if ($global:token) {
        $params.Headers = @{ Authorization = "Bearer $global:token" }
    }

    if ($Body) {
        $params.Body = ($Body | ConvertTo-Json)
    }

    try {
        return Invoke-RestMethod @params
    }
    catch {
        $errorResponse = $_.Exception.Response
        if ($errorResponse) {
            $reader = New-Object System.IO.StreamReader($errorResponse.GetResponseStream())
            $errorBody = $reader.ReadToEnd() | ConvertFrom-Json
            $reader.Close()

            Write-Host "Status: $($errorBody.status) - $($errorBody.message)" -ForegroundColor Yellow


            if ($errorResponse.StatusCode.value__ -eq 401) {
                $global:token = $null
                Write-Host "Sessão expirada ou inválida." -ForegroundColor Red
            }
        } else {
            Write-Host "Erro Crítico: $($_.Exception.Message)" -ForegroundColor Red
        }
        return $null
    }
}


function RegisterClient($name, $cpf, $phone, $email, $pass, $street, $number, $complement,
                        $neighborhood, $city, $state, $zip, $clientType, $accountType, $agency) {
    $global:token =$null

    return Request-Api `
        -Path "/api/client/register"`
        -Method POST `
        -Body @{
            name = $name
            cpf = $cpf
            phone = $phone
            email = $email
            password = $pass
            address = @{
                logradouro = $street
                numero = $number
                complemento = $complement
                bairro = $neighborhood
                cidade = $city
                estado = $state
                cep = $zip
            }
            clientType = $clientType
            accountType = $accountType
            agencyNumber = $agency
        }
}

function Login($cpf, $pass) {
    $global:token = $null
    $res = Request-Api `
        -Path "/api/auth/login"`
        -Method POST `
        -Body @{
            cpf = $cpf;
            password = $pass
        }

    if ($res) {
        $global:token = $res.token
        Write-Host "Logado com sucesso!" -ForegroundColor Green
    }
}

function GetBalance {
    return Request-Api `
        -Path "/api/account/balance"
}

function ExecuteDeposit($amount) {
    return Request-Api `
        -Path "/api/account/deposit" `
        -Method POST `
        -Body @{
            amount = $amount;
            description = "Depósito via script"
        }
}

function ExecuteWithdraw($amount) {
    return Request-Api `
        -Path "/api/account/withdraw" `
        -Method POST `
        -Body @{
            amount = $amount;
            description = "Saque via script"
        }
}

function ExecuteInternalTransaction($account, $agency, $amount) {
    return Request-Api `
        -Path "/api/account/transaction/internal" `
        -Method POST `
        -Body @{
            accountNumber = $account;
            agencyNumber = $agency;
            amount = $amount
        }
}

function ExecuteExternalTransaction($name, $account, $agency, $bank, $doc, $amount) {
    return Request-Api `
        -Path "/api/account/transaction/external" `
        -Method POST `
        -Body @{
            destinyName = $name
            destinyAccount = $account
            destinyAgency = $agency
            bankCode = $bank
            destinyDocument = $doc
            amount = $amount
        }
}

function RegisterPixKey($key, $type) {
    return Request-Api `
        -Path "/api/account/pix/register" `
        -Method POST `
        -Body @{
            key = $key;
            type = $type
        }
}

function ListPixKey {
    return Request-Api `
        -Path "/api/account/pix/keys"
}

function RemovePixKey($KeyId) {
    return Request-Api `
        -Path "/api/account/pix/$KeyId" -Method DELETE
}