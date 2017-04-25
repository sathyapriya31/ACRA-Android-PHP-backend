<?php
include "creds.php";

if (isset($_POST['username']) && isset($_POST['password'])) {
    if($_POST['username'] == $USERNAME && $_POST['password'] == $PASSWORD){
        //Defines the extension we use. No period before it, just the raw name
        $extension = ".txt";
        
        $files = glob("logs/*" . $extension);
        foreach($files as $file){
            echo $file . "\n";
            
        }
    
    }
}
?>