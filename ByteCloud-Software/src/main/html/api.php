<?php
/**
 * Created by PhpStorm.
 * User: nemmerich
 * Date: 28.09.2018
 * Time: 13:44
 */
include "auth.php";

header("Content-Type: text/plain; charset=utf-8");

if(isset($_GET['moniAll'])) {
    echo file_get_contents("http://game-chest.de:49999/?uid=".$auth."&m=dashboard&action=monitorAll");
    exit(0);
}

echo "error: unknown";