<?php
include "version.php";

if(!(isset($_SESSION['loggedIn']) && $_SESSION['loggedIn'])) {
    header('HTTP/1.1 301 Moved Permanently');
    header("Location: ".$baseUrl);
    exit(0);
}

$grid = "dashboard/".$_SESSION['grid'];
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>ByteCloud - Cloud</title>
    <link rel="stylesheet" href="https://netdna.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="<?php echo $baseUrl; ?>css/default-bytecloud.css">
    <link rel="stylesheet" href="<?php echo $baseUrl; ?>css/dashboard-bytecloud.css">
</head>
<body>
<div class="nav">
    <div class="header">
        <div class="nav-tab">
            <a href="<?php echo $baseUrl; ?>"><i class="fab fa-cloudsmith"></i></a>
            <span class="nav-tip">ByteCloud</span>
        </div>
    </div>
    <div class="body">
        <div class="nav-tab">
            <a href="<?php echo $baseUrl; ?>dashboard/"><i class="fas fa-columns"></i></a>
            <span class="nav-tip">Dashboard</span>
        </div>
        <div class="nav-tab">
            <a href="<?php echo $baseUrl; ?>dashboard/cloud/"><i class="fas fa-cloud"></i></a>
            <span class="nav-tip">Cloud</span>
        </div>
        <div class="nav-tab">
            <a href="<?php echo $baseUrl; ?>dashboard/server/"><i class="fas fa-server"></i></a>
            <span class="nav-tip">Server</span>
        </div>
        <div class="nav-tab">
            <a href="<?php echo $baseUrl; ?>dashboard/bungee/"><i class="fas fa-sitemap"></i></a>
            <span class="nav-tip">Bungee</span>
        </div>
    </div>
    <div class="footer">
        <div class="nav-tab">
            <a href="<?php echo $baseUrl; ?>?logout"><i class="fas fa-sign-out-alt"></i></a>
            <span class="nav-tip">Logout</span>
        </div>
    </div>
</div>

<?php if (!empty($grid)) {
    include $baseUrl.$grid."/site.php";
    echo $gridSite;
} ?>

<footer>
    ByteCloud - Version: <?php echo $version; ?>
</footer>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script defer src="https://use.fontawesome.com/releases/v5.0.10/js/all.js" integrity="sha384-slN8GvtUJGnv6ca26v8EzVaR9DC58QEwsIk9q1QXdCU8Yu8ck/tL/5szYlBbqmS+" crossorigin="anonymous"></script>
</body>
</html>