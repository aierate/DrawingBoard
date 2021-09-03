<?php
namespace Home\Controller;//控制器。
use Think\Controller;   //php ThinkPHP框架
class IndexController extends Controller {  //控制器类
    public function index() {
      echo "canvas test for electric brother";
	  $this->display(); //使用的是ThinkPHP框架下的获取index模版把前一行代码放到index中
	}
	public function upload_xy(){  //上传数据库中记录宽度，颜色，坐标
		 $res['status'] = 0;
		 $data['canvas_id']  = $_POST['canvas_id'];
		 $data['line_width'] = empty($_POST['line_width'])? $this->ajaxReturn($res):$_POST['line_width'];//empty检查变量是否为空
		 $data['line_style'] = empty($_POST['line_color'])? $this->ajaxReturn($res):$_POST['line_color'];
		 $data['line_sites'] = empty($_POST['line_sites'])? $this->ajaxReturn($res):$_POST['line_sites'];
		 $data['time'] = time();
		 $result = M('canvasline')->data($data)->add();//M方法直接操作数据库表，直接对上述数组中的数据进行数据库写入
		 if($result){
			 $res['status'] = 1;
		 }
		 $this->ajaxReturn($res);//接收返回值
	}
	public function get_xy(){    //
			$begin_time = empty($_POST['begin_time'])?0:(int)$_POST['begin_time'];
			$result = M('canvasline')->where('time > '.$begin_time)->select(); 
			if($result != null){
				$res['status'] = 1;
				$res['list'] = $result;
			}
			else{
				$res['status'] = 0;
			}
			$this->ajaxReturn($res);
	}
	public function clear_canvas(){
		   //$canvas_id = empty($_POST['canvas_id'])?0:(int)$_POST['canvas_id'];
		   $result = M('canvasline')->where("1")->delete();
		   if($result != 0){
				$res['status'] = 1;
				
			}
			else{
				$res['status'] = 0;
			}
			$this->ajaxReturn($res);
	}		
}