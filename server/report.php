<?php
    date_default_timezone_set("Europe/Berlin");
    $date = date('d-m-Y_H-i-s');
    $fileName = $date .'.txt';
    
    $file = fopen('logs/' . $fileName,'w') or die("An error occured writing to " . $fileName);
    foreach($_POST as $key => $value) {
        $reportLine = $key." = ".$value."\n";
        fwrite($file, $reportLine) or die ('Could not write to report file ' . $reportLine);
    }
    fclose($file);
    
    
    /*
     * Used to check if the directory is empty or not.
     * In its own function in case we need it somewhere
     * else, it is just to include this class.
     */
    function is_dir_empty($dir) {
        if (!is_readable($dir)) return NULL; 
            $handle = opendir($dir);
            while (false !== ($entry = readdir($handle))) {
                if ($entry != "." && $entry != "..") {
                return FALSE;
                }   
        }
        return TRUE;
    }
    

?>