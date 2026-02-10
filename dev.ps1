<#
.SYNOPSIS
    Helper script to manage BetterDLE Docker environment.
.DESCRIPTION
    Provides easy commands to start, stop, rebuild, and manage the Docker Compose stack.
.EXAMPLE
    .\dev.ps1 up
    Starts the containers in detached mode.
.EXAMPLE
    .\dev.ps1 rebuild
    Rebuilds the API container and restarts it.
#>

param (
    [Parameter(Mandatory=$true, Position=0)]
    [ValidateSet("up", "down", "restart", "logs", "build", "rebuild", "clean", "install", "sql-shell")]
    [string]$Command
)

$DC = "docker-compose"
$MAVEN = ".\mvnw.cmd"

switch ($Command) {
    "up" {
        Write-Host "Starting application and database..." -ForegroundColor Cyan
        & $DC up -d
    }
    "down" {
        Write-Host "Stopping services..." -ForegroundColor Cyan
        & $DC down
    }
    "restart" {
        Write-Host "Restarting services..." -ForegroundColor Cyan
        & $DC down
        & $DC up -d
    }
    "logs" {
        Write-Host "Following logs..." -ForegroundColor Cyan
        & $DC logs -f
    }
    "rebuild" {
        Write-Host "Rebuilding and restarting API container..." -ForegroundColor Cyan
        & $DC up -d --build api
    }
    "build" {
        Write-Host "Building with Docker..." -ForegroundColor Cyan
        & $DC build api
    }
    "clean" {
        Write-Host "Cleaning up containers and volumes (DATA LOSS WARNING)..." -ForegroundColor Red
        & $DC down -v
    }
    "install" {
        Write-Host "Installing dependencies..." -ForegroundColor Cyan
        & $MAVEN install -DskipTests
    }
    "sql-shell" {
        Write-Host "Connecting to Postgres shell..." -ForegroundColor Cyan
        docker exec -it betterdle-postgres psql -U betterdle -d betterdle
    }
}
