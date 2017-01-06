<?
@apache_setenv('no-gzip',1);
@ini_set('zlib.output_compression','Off');
set_time_limit(0);
ignore_user_abort();
ob_end_clean();
if($_SERVER['REQUEST_METHOD']=='PUT'){
  $md5=$_GET['md5'];
  @mkdir('test');
  $temp=sys_get_temp_dir()."/$md5";
  $path="test/$md5";
  if(file_exists($path))
    header('HTTP/1.1 304 Not Modified') or die;
  touch($temp);
  $range=$_SERVER['HTTP_CONTENT_RANGE'];
  preg_match('/(\d+)-(\d+)/',$range,$range);
  $file=fopen($temp,'r+');
  fseek($file,$range[1]);
  $data=file_get_contents('php://input');
  fwrite($file,$data,$range[2]-$range[1]);
  fclose($file);
  if($md5==md5_file($temp)){
    header('HTTP/1.1 201 Created');
    copy($temp,$path);
    unlink($temp);
    die;
  };
  header('HTTP/1.1 202 Accepted');
  header('Content-Length: 0');
  header('Connection: Close');
  flush();
  sleep(30);
  file_exists($path) or unlink($temp);
  die;
};

<script src="http://www.web-tinker.com/share/md5.js"></script>
<input type="file" />
<script>
document.querySelector("input").addEventListener("change",function(e){
  new Promise(function(resolve){  
    var fr=new FileReader;
    fr.addEventListener("load",resolve);
    fr.readAsArrayBuffer(e.target.files[0]);
  }).then(function(e){ 
    var data=new Uint8Array(e.target.result);
    var hex=Array.prototype.map.call(md5(data),function(e){
      return (e<16?"0":"")+e.toString(16);
    }).join("");
    var url="/test/"+hex;
    return new Promise(function(resolve){
      var xhr=new XMLHttpRequest;
      xhr.data=data;
      xhr.url=url;
      xhr.addEventListener("load",resolve);
      xhr.open("HEAD",url,true);
      xhr.addEventListener("error",function(){});
      xhr.send();
    });
  }).then(function(e){  
    var data=e.target.data;
    var url=e.target.url;
        if(e.target.status!=404)return Promise.resolve([
      {target:{status:201,url:url}}
    ]);
    var requests=[];
    var BLOCKSIZE=10240;
    for(var i=0;i<data.length;i+=BLOCKSIZE){
      requests.push(new Promise(function callee(i,resolve){
        var xhr=new XMLHttpRequest;
        xhr.open("PUT",url,true);
        max=Math.min(i+BLOCKSIZE+1,data.length);
        xhr.setRequestHeader("Content-Range","Bytes "+i+"-"+max);
        xhr.timeout=5000;
        xhr.url=url;
        xhr.addEventListener("timeout",callee.bind(null,i,resolve));
        xhr.addEventListener("load",resolve);
        xhr.send(new Uint8Array(data.buffer.slice(i,max)));
      }.bind(null,i)));
    };
    return Promise.all(requests);
  }).then(function(s){  
   return new Promise(function(resolve){
      s.forEach(function(e){
        if(e.target.status==201)resolve(e.target.url);
      });
    });
  }).then(function(url){  
   console.log(url);
  });
});
</script>
?>
