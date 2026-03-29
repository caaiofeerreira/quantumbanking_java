
function Login {

    $body = @{
        cpf      = $env:QB_USER
        password = $env:QB_PASS
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$env:BASE_URL/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body

    $global:token = $response.token
}

function GetBalance {

    Invoke-RestMethod -Uri "$env:BASE_URL/api/auth/account/balance" `
        -Method GET `
        -Headers @{ Authorization = "Bearer $global:token" }
}

function ExecuteDeposit {

    $body = @{
        amount      = 250.00
        description = "Depósito via script"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$env:BASE_URL/api/auth/account/deposit" `
        -Method POST `
        -ContentType "application/json" `
        -Headers @{ Authorization = "Bearer $global:token" } `
        -Body $body
}

function ExecuteWithdraw {

    $body = @{
        amount = 150.00
        description = "Saque via script"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$env:BASE_URL/api/auth/account/withdraw" `
        -Method POST `
        -ContentType "application/json" `
        -Headers @{ Authorization = "Bearer $global:token" } `
        -Body $body
}

function ExecuteInternalTransaction {

    $body = @{
        accountNumber = "73075618-1"
        amount = 100.00
        agencyNumber = "001"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$env:BASE_URL/api/auth/account/transaction/internal" `
        -Method POST `
        -ContentType "application/json" `
        -Headers @{ Authorization = "Bearer $global:token" } `
        -Body $body
}

function ExecuteExternalTransaction {

    $body = @{
        destinyName = "José Pereira"
        destinyAccount = "1345678-10"
        destinyAgency = "011"
        bankCode = "002"
        destinyDocument = "456.002.825-01"
        amount = 150.00
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$env:BASE_URL/api/auth/account/transaction/external" `
        -Method POST `
        -ContentType "application/json" `
        -Headers @{ Authorization = "Bearer $global:token" } `
        -Body $body
}

function RegisterPixKey {

    $body = @{
        key = "maria@empresa.com.br"
        type = "EMAIL"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$env:BASE_URL/api/auth/account/pix/register" `
        -Method POST `
        -ContentType "application/json" `
        -Headers @{ Authorization = "Bearer $global:token" } `
        -Body $body
}

function ListPixKey {

    Invoke-RestMethod -Uri "$env:BASE_URL/api/auth/account/pix/keys" `
        -Method GET `
        -Headers @{ Authorization = "Bearer $global:token" }
}

function RemovePixKey($KeyId) {
    Invoke-RestMethod -Uri "$env:BASE_URL/api/auth/account/pix/$KeyId" `
        -Method DELETE `
        -Headers @{ Authorization = "Bearer $global:token" }
}