<?php
/**
 * Created by PhpStorm.
 * User: nemmerich
 * Date: 06.09.2018
 * Time: 15:30
 */

$monitor = explode(":", file_get_contents("http://game-chest.de:49999/?uid=$auth&m=dashboard&monitor=all"));

$gridSite = <<<GRID
<div class="grid">
    <div class="row">
        <div class="col-md-4">
            <i class="fa fa-cloud usage-icon" aria-hidden="true"></i>System-CPU-Load:
            <div class="progress">
                <div id="cpu-usage" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="$monitor[0]"
                     aria-valuemin="0" aria-valuemax="100" style="width: 63%">
                    $monitor[0]
                </div>
            </div>
            <i class="fa fa-server usage-icon" aria-hidden="true"></i>System-Memory-Load:
            <div class="progress">
                <div id="ram-usage" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="$monitor[1]"
                     aria-valuemin="0" aria-valuemax="100" style="width: 86%">
                    $monitor[1]
                </div>
            </div>
            <i class="fa fa-hdd usage-icon" aria-hidden="true"></i>Storage:
            <div class="progress">
                <div id="ssd-storage" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="$monitor[2]"
                     aria-valuemin="0" aria-valuemax="100" style="width: 49%">
                    $monitor[2]
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
GRID;
