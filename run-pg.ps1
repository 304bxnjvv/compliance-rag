# Carga las variables de .env y arranca con el perfil "pg" (pgvector en Supabase).
# Uso:  ./run-pg.ps1

if (-not (Test-Path .env)) {
    Write-Host "Falta el archivo .env. Copia .env.example a .env y pon tus claves." -ForegroundColor Yellow
    exit 1
}

Get-Content .env | Where-Object { $_ -match '^\s*[^#].*=' } | ForEach-Object {
    $name, $value = $_ -split '=', 2
    Set-Item -Path "env:$($name.Trim())" -Value $value.Trim()
}

$env:SPRING_PROFILES_ACTIVE = "pg"
Write-Host "Variables cargadas. Arrancando con pgvector (perfil pg)..." -ForegroundColor Green
mvn spring-boot:run
