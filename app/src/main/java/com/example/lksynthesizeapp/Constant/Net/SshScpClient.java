package com.example.lksynthesizeapp.Constant.Net;

import android.os.Environment;
import android.util.Log;

import com.example.lksynthesizeapp.Constant.Base.CallBack;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Vector;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SshScpClient {
    public void scpFile(CallBack allBack){
        Connection con = new Connection("192.168.43.251", 22); //可以不输入端口号
        //连接
        try {
            con.connect();
            boolean isAuthed = con.authenticateWithPassword("root", "root");
            //建立SCP客户端
            SCPClient scpClient = con.createSCPClient();
            //服务器端的文件下载到本地的目录下
            //scpClient.get("/home/test/11.txt", "C:/");
            //将本地文件上传到服务器端的目录下
            scpClient.put(Environment.getExternalStorageDirectory() + "/LUKESSH/luke-ssh.bin", "/tmp");
            //建立会话，一个会话内只能执行一个linux命令
            Session session = null;
            session = con.openSession();
            //利用会话可以操作远程服务器
            //例如：删除远程目录下的文件
            //session.execCommand("rm -f".concat(remotePath).concat(qrCodeFileMode));
            //显示执行命令后的信息
            InputStream stdout = new StreamGobbler(session.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
//        while (true) {
//            String line = br.readLine()+"";
//            if (line == null) {
//                Log.e("SCP", "远程服务器返回信息" );
//                break;
//            }
//            Log.e("SCP", line);
//        }
            session.close();
            con.close();
            execute("sysupgrade /tmp/luke-ssh.bin \n");
            allBack.confirm("升级成功");
        } catch (IOException e) {
            e.printStackTrace();
            allBack.cancel();
        }
        //远程服务器的用户名密码

//        SSHExcuteCommandHelper.writeBefor(Constant.URL, "sysupgrade /tmp/luke-ssh.bin\n", new SSHCallBack() {
//            @Override
//            public void confirm(String data) {
//                Log.e("XXX", data);
//            }
//
//            @Override
//            public void error(String s) {
//                Log.e("XXX", "error");
//            }
//        });
    }


    /**
     * 执行相关的命令（交互式）
     * @param command
     * @return
     */
    public int execute(String command) throws IOException {
        com.jcraft.jsch.Session session = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession("root", "192.168.43.251", 22);
            //设置登录主机的密码
            session.setPassword("root");
            //如果服务器连接不上，则抛出异常
            if (session == null) {
                throw new Exception("session is null");
            }
            //设置首次登录跳过主机检查
            session.setConfig("StrictHostKeyChecking", "no");
            //设置登录超时时间
            session.connect(3000);
        } catch (Exception e) {
            Log.e("XXX",e.toString());
        }

        int returnCode = 0;
        ChannelShell channel = null;
        PrintWriter printWriter = null;
        BufferedReader input = null;
        Vector<String> stdout = new Vector<String>();
        try {
            //建立交互式通道
            channel = (ChannelShell) session.openChannel("shell");
            channel.connect();

            //获取输入
            InputStreamReader inputStreamReader = new InputStreamReader(channel.getInputStream());
            input = new BufferedReader(inputStreamReader);

            //输出
            printWriter = new PrintWriter(channel.getOutputStream());
            printWriter.println(command);
            printWriter.println("exit");
            printWriter.flush();
            String line;
            while ((line = input.readLine()) != null) {
                stdout.add(line);
                System.out.println(line);
            }
            Log.e("XXX",line);
        } catch (Exception e) {
            Log.e(e.getMessage(),e.toString());
            return -1;
        }finally {
            printWriter.close();
            input.close();
            session.disconnect();
            if (channel != null) {
                //关闭通道
                channel.disconnect();
            }
        }
        return returnCode;
    }
}
