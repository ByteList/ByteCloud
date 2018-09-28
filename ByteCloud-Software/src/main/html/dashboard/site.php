<?php
/**
 * Created by PhpStorm.
 * User: nemmerich
 * Date: 06.09.2018
 * Time: 15:30
 */

$monitor = explode(":", file_get_contents("http://game-chest.de:49999/?uid=".$auth."&m=dashboard&monitor=all"));

$gridSite = <<<GRID
<div class="grid">
    <div class="row">
        <div class="col-md-4">
            <i class="fa fa-cloud usage-icon" aria-hidden="true"></i>System-CPU-Load:
            <div class="progress">
                <div id="sys-cpu-load" class="progress-bar progress-bar-striped active" role="progressbar" style="width: $monitor[0]%">
                    $monitor[0]%
                </div>
            </div>
            <i class="fa fa-server usage-icon" aria-hidden="true"></i>System-Memory-Load:
            <div class="progress">
                <div id="sys-ram-load" class="progress-bar progress-bar-striped active" role="progressbar" style="width: $monitor[1]%">
                    $monitor[1]%
                </div>
            </div>
            <i class="fa fa-hdd usage-icon" aria-hidden="true"></i>Storage:
            <div class="progress">
                <div id="storage" class="progress-bar progress-bar-striped active" role="progressbar" style="width: $monitor[2]%">
                    $monitor[2]%
                </div>
            </div>
        </div>
        <div class="col-md-4">
        </div>
        <div class="col-md-4">
        </div>
    </div>
    <div class="row">
        <div class="col-md-4">
        </div>
        <div class="col-md-4">
        </div>
        <div class="col-md-4">
        </div>
    </div>
</div>
<script src="$baseUrl/js/dashboard-bytecloud.js"></script>
<script>
    updateMonitor("$baseUrl/api.php?moniAll");
</script>
GRID;
