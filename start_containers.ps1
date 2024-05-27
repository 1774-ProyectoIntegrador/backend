# File name: start_containers.ps1

# Check if docker-compose is installed
if (-not (Get-Command docker-compose -ErrorAction SilentlyContinue)) {
    Write-Host "[JACKMOON-DEV] docker-compose is not installed. Please install it and try again."
    exit 1
}

# Ensure correct permissions for directories and files
Write-Host "[JACKMOON-DEV] Setting permissions for directories and files..."

# Note: In Windows, setting permissions is different and complex. You might need to adjust this part based on your requirements.
# The below commands are placeholders, adjust them according to your actual needs.

# Example of changing file attributes (not permissions)
Set-ItemProperty -Path "./docker" -Name "IsReadOnly" -Value $false -ErrorAction Stop
Set-ItemProperty -Path "./sql" -Name "IsReadOnly" -Value $false -ErrorAction Stop
Set-ItemProperty -Path "./clean.sh" -Name "IsReadOnly" -Value $false -ErrorAction Stop

if ($?) {
    Write-Host "[JACKMOON-DEV] Error while setting permissions."
    exit 1
}

# Stop and remove existing containers
Write-Host "[JACKMOON-DEV] Stopping and removing existing containers..."
docker-compose down
Start-Sleep -Seconds 10  # Adding a delay to ensure proper shutdown

if ($?) {
    Write-Host "[JACKMOON-DEV] Error while stopping and removing containers."
    exit 1
}

# Build the containers
Write-Host "[JACKMOON-DEV] Building the containers..."
docker-compose build

if ($?) {
    Write-Host "[JACKMOON-DEV] Error during container build."
    exit 1
}

# Start the containers in the background
Write-Host "[JACKMOON-DEV] Starting the containers..."
docker-compose up -d

if ($?) {
    Write-Host "[JACKMOON-DEV] Error while starting the containers."
    exit 1
}

Write-Host "[JACKMOON-DEV] Containers started successfully."