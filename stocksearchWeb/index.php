<?php if(isset($_GET["input"])): ?>
<?php 
	$jsonfile=file_get_contents("http://dev.markitondemand.com/MODApis/Api/v2/Lookup/jsonp?&callback=".$_GET["callback"]."&input=".urlencode($_GET['input']));
	echo $jsonfile;
?>
<?php elseif(isset($_GET["symbol"])): ?>
<?php 
	$jsonfile=file_get_contents("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=".urlencode($_GET['symbol']));
	echo $_GET["callback"].'('.json_encode($jsonfile).')';
?>
<?php elseif(isset($_GET["symbols"])): ?>
<?php 
	$jsonfile=file_get_contents("http://dev.markitondemand.com/MODApis/Api/v2/InteractiveChart/json?parameters={\"Normalized\":false,\"NumberOfDays\":1095,\"DataPeriod\":\"Day\",\"Elements\":[{\"Symbol\":\"".$_GET['symbols']."\",\"Type\":\"price\",\"Params\":[\"ohlc\"]}]}");
	echo $_GET["callback"].'('.json_encode($jsonfile).')';
?>
<?php elseif(isset($_GET["symbolss"])): ?>
<?php 
	$jsonfile=file_get_contents("https://ajax.googleapis.com/ajax/services/search/news?v=1.0&q=".$_GET["symbolss"]."&userip=http://www-scf.usc.edu/~zaranade/HW8.html");
	echo $_GET["callback"].'('.json_encode($jsonfile).')';
?>
<?php elseif(isset($_GET["symbolsss"])): ?>
<?php 
	$accountKey='iFuNKUxkaOaNxS0Lg9t+UoXHr7NY1Lnv/rejzF/tGI8';
	$symbol = $_GET['symbolsss'];
    $url = "https://api.datamarket.azure.com/Bing/Search/v1/News?Query=";
    $request = $url.urlencode( '\'' .$symbol. '\'');
    $request .= '&$format=json';
	$context = stream_context_create(array('http' => array(
                    'request_fulluri' => true,
                    'header'  => "Authorization: Basic " . base64_encode($accountKey . ":" . $accountKey)
                    )
                    ));
    
    $jsonfile = file_get_contents($request, 0, $context);
	echo $_GET["callback"].'('.json_encode($jsonfile).')';
	endif;
?>

