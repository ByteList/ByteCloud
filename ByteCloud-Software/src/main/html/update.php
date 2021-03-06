<?php
session_start();
/**
 * Created by PhpStorm.
 * User: nemmerich
 * Date: 07.09.2018
 * Time: 10:18
 */
include "config.php";

if(!(isset($_SESSION['loggedIn']) && $_SESSION['loggedIn'])) {
    header('HTTP/1.1 301 Moved Permanently');
    header("Location: ".$baseUrl);
    exit(0);
}

if(isset($_POST['uc']) && isset($_POST['k'])) {
    $key = $_POST['k'];
    $updateChannel = $_POST['uc'];

    if($key == "!updateByteCloud") {
        $rootPath = realpath('./');
        $backup = new ZipArchive();
        $res = $backup->open('backup.zip', ZipArchive::CREATE | ZipArchive::OVERWRITE);

        if($res) {
            /** @var SplFileInfo[] $files */
            $files = new RecursiveIteratorIterator(new RecursiveDirectoryIterator($rootPath),RecursiveIteratorIterator::LEAVES_ONLY);
            foreach ($files as $name => $file) {
                if (!$file->isDir()) {
                    $filePath = $file->getRealPath();
                    $relativePath = substr($filePath, strlen($rootPath) + 1);

                    $backup->addFile($filePath, $relativePath);
                }
            }
            $backup->close();
            $file = "./html.zip";

            $ch = curl_init();
            if($updateChannel == "s") {
                curl_setopt($ch, CURLOPT_URL, "https://kvm.bytelist.de/jenkins/job/ByteCloud-v2/lastSuccessfulBuild/artifact/html.zip");
            } else {
                curl_setopt($ch, CURLOPT_URL, "https://kvm.bytelist.de/jenkins/job/ByteCloud-v2-dev/lastSuccessfulBuild/artifact/html.zip");
            }
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_USERPWD, "apiUser:Uf6UYSqSrgOGby01fSIe7dAkd1eSzVYggqH");
            curl_setopt($ch, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
            $rawFile = curl_exec($ch);

            if(curl_errno($ch)){
                $err = curl_error($ch);
            } else {
                file_put_contents($file, $rawFile);
                $zip = new ZipArchive();
                $res = $zip->open($file);
                if ($res === TRUE) {
                    rename("./auth.php", "./saved_auth.php");

                    $zip->extractTo('./');
                    $zip->close();

                    unlink($file);

                    unlink("./auth.php");
                    rename("./saved_auth.php", "./auth.php");

                    $info = "Successful updated!";
                } else {
                    $err = "Error while unzip!";
                }
            }

            curl_close($ch);

//            if(move_uploaded_file($_FILES['html_content_file']['tmp_name'], "./".$file)) {
//                $zip = new ZipArchive();
//                $res = $zip->open($file);
//                if ($res === TRUE) {
//                    rename("./auth.php", "./saved_auth.php");
//
//                    $zip->extractTo('./');
//                    $zip->close();
//
//                    unlink($file);
//
//                    unlink("./auth.php");
//                    rename("./save_auth.php", "./auth.php");
//
//                    $info = "Successful updated!";
//                } else {
//                    $err = "Error while unzip!";
//                }
//            } else {
//                $err = "Error while uploading!";
//            }
        } else {
            $err = "Error while creating backup!";
        }
    } else {
        $err = "Wrong password!";
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>ByteCloud - Update</title>
    <link rel="stylesheet" href="https://netdna.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/default-bytecloud.css">
    <link rel="stylesheet" href="css/dashboard-bytecloud.css">
</head>
<body>
<div class="container">
    <div class="login">
        <form enctype="multipart/form-data" class="login-form" method="post">
            <?php if($err != "") echo "<span class='c-err' style='margin-bottom: 15px;'>".$err."</span>"; ?>
            <?php if($info != "") echo "<span class='c-info' style='margin-bottom: 15px;'>".$info."</span>"; ?>
            <h3>Update Webinterface</h3>
<!--            <input type="file" placeholder="html.zip file" name="html_content_file"/>-->
            <label style="display: block; font-weight: normal;">
                <select name="uc">
                    <option value="s">UpdateChannel: Stable</option>
                    <option value="d">UpdateChannel: Dev</option>
                </select>
            </label>
            <input type="password" placeholder="Key" name="k"/>
            <button>update</button>
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