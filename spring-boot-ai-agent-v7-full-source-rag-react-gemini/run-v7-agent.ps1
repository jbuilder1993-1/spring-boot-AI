if (-not $env:GEMINI_API_KEY) { Write-Host 'Set GEMINI_API_KEY first'; exit 1 }
mvn spring-boot:run
