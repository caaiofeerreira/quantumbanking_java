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
        $params.Body = ($Body | ConvertTo-Json -Depth 10)
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
                Write-Host "Sessao expirada ou invalida." -ForegroundColor Red
            }
        } else {
            Write-Host "Erro Critico: $($_.Exception.Message)" -ForegroundColor Red
        }
        return $null
    }
}

#ADMIN
function RegisterAgency($agencyName, $agencyNumber, $phone,
                       $street, $number, $complement, $neighborhood, $city, $state, $zip, $compe) {

    return Request-Api `
        -Path "/api/admin/agency/register"`
        -Method POST `
        -Body @{
            agencyName = $agencyName
            agencyNumber = $agencyNumber
            phone = $phone
            address = @{
                street = $street
                number = $number
                complement = $complement
                neighborhood = $neighborhood
                city = $city
                state = $state
                zipCode = $zip
            }
             compe = $compe
        }
}

function RegisterManager($name, $cpf, $phone, $email, $pass,
                        $street, $number, $complement, $neighborhood, $city, $state, $zip, $agencyNumber) {

    return Request-Api `
        -Path "/api/admin/manager/register"`
        -Method POST `
        -Body @{
            name = $name
            cpf = $cpf
            phone = $phone
            email = $email
            password = $pass
            address = @{
                street = $street
                number = $number
                complement = $complement
                neighborhood = $neighborhood
                city = $city
                state = $state
                zipCode = $zip
            }
            agencyNumber = $agencyNumber
        }
}

#MANAGER
function ManagerProfile {
     return Request-Api `
        -Path "/api/manager/profile" `
        -Method GET
}

function ListAccounts {
    return Request-Api `
        -Path "/api/manager/my-agency/accounts" `
        -Method GET
}


#CLIENT
function RegisterClient($name, $cpf, $phone, $email, $pass,
                        $street, $number, $complement, $neighborhood, $city, $state, $zip,
                        $clientType, $accountType, $agencyNumber) {

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
                street = $street
                number = $number
                complement = $complement
                neighborhood = $neighborhood
                city = $city
                state = $state
                zipCode = $zip
            }
            clientType = $clientType
            accountType = $accountType
            agencyNumber = $agencyNumber
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
        Write-Host "Logado com sucesso!"-ForegroundColor Green
    }
}

function MyAccounts {
    return Request-Api `
        -Path "/api/account/my-accounts" `
        -Method GET
}


function ClientProfile {
    return Request-Api `
        -Path "/api/account/profile" `
        -Method GET
}

function RegisterPixKey($accountNumber, $key) {
    return Request-Api `
        -Path "/api/account/$accountNumber/pix/keys" `
        -Method POST `
        -Body @{
            key = $key;
        }
}

function ListPixKey($accountNumber) {
    return Request-Api `
        -Path "/api/account/$accountNumber/pix/keys" `
        -Method GET
}

function RemovePixKey($accountNumber, $keyId) {
    return Request-Api `
        -Path "/api/account/$accountNumber/pix/keys/$KeyId" `
        -Method DELETE
}

function GetBalance($accountNumber) {
    return Request-Api `
        -Path "/api/account/$accountNumber/balance" `
        -Method GET
}

function ExecuteDeposit($accountNumber, $amount) {
    return Request-Api `
        -Path "/api/account/$accountNumber/transaction/deposit" `
        -Method POST `
        -Body @{
            amount = $amount;
            description = "Deposito via script"
        }
}

function ExecuteWithdraw($accountNumber, $amount) {
    return Request-Api `
        -Path "/api/account/$accountNumber/transaction/withdraw" `
        -Method POST `
        -Body @{
            amount = $amount;
            description = "Saque via script"
        }
}

function ExecuteInternalTransaction($accountNumber, $destinationAccountNumber, $agencyNumber, $amount, $description) {
    return Request-Api `
        -Path "/api/account/$accountNumber/transaction/internal" `
        -Method POST `
        -Body @{
            destinationAccountNumber = $destinationAccountNumber;
            agencyNumber = $agencyNumber;
            amount = $amount
            description = $description
        }
}

function ExecuteExternalTransaction($accountNumber, $destinationName, $destinationAccount, $destinationAgency, $compe, $destinationDocument, $amount, $description) {
    return Request-Api `
        -Path "/api/account/$accountNumber/transaction/external" `
        -Method POST `
        -Body @{
            destinationName = $destinationName
            destinationAccount = $destinationAccount
            destinationAgency = $destinationAgency
            compe = $compe
            destinationDocument = $destinationDocument
            amount = $amount
            description = $description
        }
}

function ExecutePixTransaction($accountNumber, $key, $amount, $description) {
    return Request-Api `
        -Path "/api/account/$accountNumber/transaction/pix" `
        -Method POST `
        -Body @{
            key = $key
            amount = $amount
            description = $description
        }
}