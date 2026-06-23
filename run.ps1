# Carga las variables de .env y arranca la aplicacion.
# Uso:  ./run.ps1

if (-not (Test-Path .env)) {
    Write-Host "Falta el archivo .env. Copia .env.example a .env y pon tus claves." -ForegroundColor Yellow
    exit 1
}

Get-Content .env | Where-Object { $_ -match '^\s*[^#].*=' } | ForEach-Object {
    $name, $value = $_ -split '=', 2
    Set-Item -Path "env:$($name.Trim())" -Value $value.Trim()
}

Write-Host "Variables cargadas. Arrancando compliance-rag..." -ForegroundColor Green
mvn spring-boot:run
