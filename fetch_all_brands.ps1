# PowerShell script to fetch all brands from the API

$apiBaseUrl = "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1"
$endpoint = "$apiBaseUrl/master/brands"

$allBrands = @()
$pageSize = 100
$offset = 0
$pageNum = 1
$totalFetched = 0

Write-Host "Fetching all brands from API..." -ForegroundColor Cyan
Write-Host "API: $endpoint" -ForegroundColor Gray
Write-Host ""

do {
    try {
        $url = "$endpoint`?limit=$pageSize&offset=$offset"
        Write-Host "Page $pageNum - Fetching (offset: $offset)..." -ForegroundColor Yellow
        
        $response = Invoke-RestMethod -Uri $url -Method Get -TimeoutSec 10
        
        if ($response.status -eq "success" -and $response.data.data) {
            $brands = $response.data.data
            $allBrands += $brands
            $totalFetched += $brands.Count
            
            Write-Host "  Loaded $($brands.Count) brands (Total: $totalFetched)" -ForegroundColor Green
            
            # Check if there are more pages
            $pagination = $response.data.pagination
            if ($pagination -and $totalFetched -ge $pagination.total) {
                Write-Host "  Reached end of data (Total: $($pagination.total))" -ForegroundColor Green
                break
            }
            
            $offset += $pageSize
            $pageNum++
        } else {
            Write-Host "  Invalid response format" -ForegroundColor Red
            break
        }
    } catch {
        Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
        break
    }
    
    Start-Sleep -Milliseconds 200
    
} while ($true)

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "ALL BRANDS - Total: $($allBrands.Count)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Display brands
$counter = 1
foreach ($brand in $allBrands) {
    $active = if ($brand.is_active) { "[ACTIVE]" } else { "[INACTIVE]" }
    $featured = if ($brand.is_featured) { "[FEATURED]" } else { "" }
    Write-Host "$counter. $($brand.name) $active $featured (ID: $($brand.id))"
    $counter++
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$activeBrands = ($allBrands | Where-Object { $_.is_active } | Measure-Object).Count
$featuredBrands = ($allBrands | Where-Object { $_.is_featured } | Measure-Object).Count

Write-Host "Total Brands: $($allBrands.Count)" -ForegroundColor Green
Write-Host "Active Brands: $activeBrands" -ForegroundColor Green
Write-Host "Featured Brands: $featuredBrands" -ForegroundColor Green
Write-Host ""

# Save to JSON file
$jsonPath = "d:\Download\Beckie-Deal\all_brands.json"
$allBrands | ConvertTo-Json | Out-File -FilePath $jsonPath -Encoding UTF8
Write-Host "Brand data saved to: $jsonPath" -ForegroundColor Cyan

Write-Host ""
Write-Host "Done!" -ForegroundColor Green
