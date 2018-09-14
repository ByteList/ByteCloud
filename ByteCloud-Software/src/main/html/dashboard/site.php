<?php
/**
 * Created by PhpStorm.
 * User: nemmerich
 * Date: 06.09.2018
 * Time: 15:30
 */
$gridSite = <<<GRID
<div class="grid">
    <div class="row">
        <div class="col-md-4">
            <i class="fa fa-cloud usage-icon" aria-hidden="true"></i>CPU-Usage:
            <div class="progress">
                <div id="cpu-usage" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="63"
                     aria-valuemin="0" aria-valuemax="100" style="width: 63%">
                    63%
                </div>
            </div>
            <i class="fa fa-server usage-icon" aria-hidden="true"></i>RAM-Usage:
            <div class="progress">
                <div id="ram-usage" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="86"
                     aria-valuemin="0" aria-valuemax="100" style="width: 86%">
                    86%
                </div>
            </div>
            <i class="fa fa-database usage-icon" aria-hidden="true"></i>MongoDB-Storage:
            <div class="progress">
                <div id="mdb-storage" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="12"
                     aria-valuemin="0" aria-valuemax="100" style="width: 12%">
                    12%
                </div>
            </div>
            <i class="fa fa-database usage-icon" aria-hidden="true"></i>MySQL-Storage:
            <div class="progress">
                <div id="mysql-storage" class="progress-bar progress-bar-striped active" role="progressbar"
                     aria-valuenow="8" aria-valuemin="0" aria-valuemax="100" style="width: 8%">
                    8%
                </div>
            </div>
            <i class="fa fa-hdd usage-icon" aria-hidden="true"></i>SSD-Storage:
            <div class="progress">
                <div id="ssd-storage" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="49"
                     aria-valuemin="0" aria-valuemax="100" style="width: 49%">
                    49%
                </div>
            </div>
            <i class="fa fa-cloud usage-icon" aria-hidden="true"></i>Temporary Server-Usage:
            <div class="progress">
                <div id="temp-server-usage" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="63"
                     aria-valuemin="0" aria-valuemax="100" style="width: 63%">
                    63%
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
