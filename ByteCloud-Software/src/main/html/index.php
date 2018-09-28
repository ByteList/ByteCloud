<?php
session_start();
include "version.php";

if(isset($_GET['v']) && isset($_GET['u'])) {
    include "auth.php";
    header("Content-Type: text/plain; charset=utf-8");
    if($_GET['v'] == $auth) {
        try {
            $fo = fopen("./auth.php", "w");
            fwrite($fo, "<?php \$auth = \"".$_GET['u']."\";");
            fclose($fo);
            echo "ok";
        } catch (Exception $ex) {
            echo "error: ".$ex->getTraceAsString();
        }
    } else {
        echo "not-authenticated";
    }
    exit(0);
}

if(isset($_GET['logout'])) {
    session_destroy();
    header('HTTP/1.1 301 Moved Permanently');
    header("Location: ".$baseUrl);
    exit(0);
}

if(isset($_SESSION['loggedIn']) && $_SESSION['loggedIn']) {
    header('HTTP/1.1 301 Moved Permanently');
    header("Location: ".$baseUrl."dashboard/");
    exit(0);
}

if($_POST['u'] != null && $_POST['p'] != null) {
    $user = $_POST['u'];
    $password = $_POST['p'];

    if($user == "ByteList" && $password == "1") {
        $_SESSION['loggedIn'] = true;
        $_SESSION['u'] = $user;
        header('HTTP/1.1 301 Moved Permanently');
        header("Location: ".$baseUrl);
        exit(0);
    } else {
        $err = "Wrong username or password!";
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>ByteCloud - Dashboard</title>
    <link rel="stylesheet" href="https://netdna.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/default-bytecloud.css">
    <link rel="stylesheet" href="css/dashboard-bytecloud.css">
</head>
<body>
<div class="container">
    <div class="login">
        <form class="login-form" method="post">
            <?php if($err != "") echo "<span class='c-err' style='margin-bottom: 15px;'>".$err."</span>"; ?>
            <?php if($info != "") echo "<span class='c-info' style='margin-bottom: 15px;'>".$info."</span>"; ?>
            <h3>Login</h3>
            <input type="text" placeholder="Username" name="u" value="<?php echo $user ?>"/>
            <input type="password" placeholder="Password" name="p"/>
            <button>login</button>
        </form>
    </div>
</div>

<footer>
    ByteCloud - Version: <?php echo $version; ?>
</footer>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script defer src="https://use.fontawesome.com/releases/v5.0.10/js/all.js" integrity="sha384-slN8GvtUJGnv6ca26v8EzVaR9DC58QEwsIk9q1QXdCU8Yu8ck/tL/5szYlBbqmS+" crossorigin="anonymous"></script>
</body>
</html>