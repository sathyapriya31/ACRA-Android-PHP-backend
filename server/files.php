<?php
//Not password protected. This script only tells the connector if there are files there, not the contents of them
$files = glob("logs/*" . $extension);
$count = 0;

foreach($files as $file){
    $count++;
}

if($count >= 1){
    echo "true";
}else{
    echo "false";
}


?>