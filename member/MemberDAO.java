package net.member;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.print.attribute.SetOfIntegerSyntax;

import net.utility.DBClose;
import net.utility.DBOpen;
import net.utility.MyAuthenticator;

public class MemberDAO { //Data Access Object
                         //데이터베이스 비지니스 로직 구현
    
    private DBOpen dbopen=null;
    private Connection con=null;
    private PreparedStatement pstmt=null;
    private ResultSet rs=null;
    private StringBuilder sql=null;
    
    public MemberDAO() {
        dbopen=new DBOpen();
    }//end
    
    
    public String loginProc(MemberDTO dto) {
        String mlevel=null;
        try {
            con=dbopen.getConnection();
            
            sql=new StringBuilder();
            sql.append(" SELECT mlevel ");
            sql.append(" FROM member ");
            sql.append(" WHERE id=? AND passwd=? ");
            sql.append(" AND mlevel in ('A1', 'B1', 'C1', 'D1') ");
            
            pstmt=con.prepareStatement(sql.toString());
            pstmt.setString(1, dto.getId());
            pstmt.setString(2, dto.getPasswd());
            
            rs= pstmt.executeQuery();            
            if(rs.next()) {
                mlevel=rs.getString("mlevel");
            }//if end
            
        }catch (Exception e) {
            System.out.println("로그인실패:" + e);
        }finally {
            DBClose.close(con, pstmt, rs);
        }//end
        return mlevel;
    }//loginProc() end
    
    public int duplecateID(String id) {
        int cnt=0;
        try {
            con=dbopen.getConnection();
            
            sql=new StringBuilder();
            sql.append(" SELECT COUNT(id) as cnt ");
            sql.append(" FROM member ");
            sql.append(" WHERE id=?  ");
           
            
            pstmt=con.prepareStatement(sql.toString());
            pstmt.setString(1, id);
           
            
            rs= pstmt.executeQuery();            
            if(rs.next()) {
               cnt=rs.getInt("cnt");
            }//if end
            
        }catch (Exception e) {
            System.out.println("아이디 중복 확인 실패:" + e);
        }finally {
            DBClose.close(con, pstmt, rs);
        }//end
        return cnt;           
        
    }//duplecateID() end
    
    public int duplecateEmail(String email) {
        int cnt=0;
        try {
            con=dbopen.getConnection();
            
            sql=new StringBuilder();
            sql.append(" SELECT COUNT(email) as cnt ");
            sql.append(" FROM member ");
            sql.append(" WHERE email=?  ");
           
            
            pstmt=con.prepareStatement(sql.toString());
            pstmt.setString(1, email);
           
            
            rs= pstmt.executeQuery();            
            if(rs.next()) {
               cnt=rs.getInt("cnt");
            }//if end
            
        }catch (Exception e) {
            System.out.println("이메일 중복 확인 실패:" + e);
        }finally {
            DBClose.close(con, pstmt, rs);
        }//end
        return cnt;           
        
    }//duplecateID() end
    
    
    public int create(MemberDTO dto) {
    	 int cnt=0;
         try {
             con=dbopen.getConnection();
             
             sql=new StringBuilder();
             //sql.append(" INSERT INTO member(id, passwd, mname, tel, email, zipcode, address1, address2, job, mlevel, mdate) ");
            //sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'D1', sysdate) ");
             sql.append(" INSERT INTO member(id, passwd, mname, tel, email, zipcode, address1, address2, job, mlevel, mdate) ");
             sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'D1', now()) ");
            
             
             pstmt=con.prepareStatement(sql.toString());
             pstmt.setString(1, dto.getId());
             pstmt.setString(2, dto.getPasswd());
             pstmt.setString(3, dto.getMname());
             pstmt.setString(4, dto.getTel());
             pstmt.setString(5, dto.getEmail());
             pstmt.setString(6, dto.getZipcode());
             pstmt.setString(7, dto.getAddress1());
             pstmt.setString(8, dto.getAddress2());
             pstmt.setString(9, dto.getJob());
           
             cnt= pstmt.executeUpdate();
             
            
         }catch (Exception e) {
             System.out.println("회원 가입 실패: " + e);
         }finally {
             DBClose.close(con, pstmt);
         }//end
         return cnt;           
    }//create() end
    
    public boolean findID (MemberDTO dto) {              
    	boolean flag = false;
    	try {
    		con=dbopen.getConnection();          
            sql=new StringBuilder();
    		
            //이름과 이메일이 일치하는 ID 가져오기
            sql.append(" SELECT id ");
            sql.append(" FROM member ");
            sql.append(" WHERE mname=? AND email=? ");
            pstmt=con.prepareStatement(sql.toString());
            pstmt.setString(1, dto.getMname());
            pstmt.setString(2, dto.getEmail());
            rs=pstmt.executeQuery();
    		if(rs.next()) { //이름과 이메일 일치되었다면
    			
    			//1) 아이디 가져오기
    			String id=rs.getString("id");
    			
    			//2) 임시비밀번호 발급
    			//[임시비밀번호발급]
    			// ->대문자, 소문자, 숫자를 이용해서 랜덤하게 10글자로 만들기
    			String[] ch = {
    					"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
    					"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
    					"0","1","2","3","4","5","6","7","8","9"
    			};//ch[0]~ch[61]
    			
    			//ch배열에서 랜덤하게 10글자 뽑아서 가져오기
    			StringBuilder imsiPW = new StringBuilder();//임시비밀번호
    			for(int i=0; i<10; i++) {
    				int num=(int)(Math.random()*ch.length);
    				imsiPW.append(ch[num]);
    			}//for end
    			
    			//임시 비밀번호로 테이블 수정하기
    			 sql=new StringBuilder();
	    		 sql.append(" UPDATE member ");
	             sql.append(" SET passwd=? ");
	             sql.append(" WHERE mname=? AND email=? ");
	             pstmt=con.prepareStatement(sql.toString());
	             pstmt.setString(1, imsiPW.toString());//임시비밀번호
	             pstmt.setString(2, dto.getMname());
	             pstmt.setString(3, dto.getEmail());
	             
	             int cnt=pstmt.executeUpdate();
	             if(cnt==1) {// 임시 비밀번호로 테이블 수정되었다면
	            	 //아이디(id)와 임시비밀번호(imsiPW)를 웹 메일 전송하기
	            	 
	            	 //메일내용
	            	String content="※ 임시 비밀번호로 로그인 한 후, 회원 정보 수정에서 비밀번호를 변경하시기 바랍니다";
	            	content += "<hr>";
	         		content += "<table border='1'>";
	         		content += "<tr>";
	         		content += "	<th>아이디</th>";
	         		content += "	<td>" + id + "</td>";		
	         		content += "</tr>";
	         		content += "<tr>";
	         		content += "	<td>임시비밀번호</td>";
	         		content += "	<td>" + imsiPW.toString() + "</td>";
	         		content += "</table>";
	            	 
	         		content += "<hr>";
	        		content += "<img src='http://localhost:9090/myweb/images/캐릭터불.jpeg'>";
	        		
	         		
	         		
	         		String mailServer = "mw-002.cafe24.com";//cafe24 메일서버
	        		Properties props = new Properties();
	        		props.put("mail.smtp.host", mailServer);
	        		props.put("mail.smtp.auth", true);
	        		
	        		Authenticator myAuth = new MyAuthenticator(); //다형성
	        		Session sess = Session.getInstance(props, myAuth);
	        		
	        		InternetAddress[] address = { new InternetAddress(dto.getEmail()) };
	        		Message msg = new MimeMessage(sess);
	        		msg.setRecipients(Message.RecipientType.TO, address);
	          		msg.setFrom(new InternetAddress("webmaster@itwill.co.kr"));
	        		msg.setSubject("MyWeb 아이디/비번 입니다");
	           		msg.setContent(content, "text/html; charset=UTF-8");      
	        		msg.setSentDate(new Date());                            
	        		Transport.send(msg);
	             
	        		flag=true;//아이디+비번찾기 최종적으로 성공

	             }//if end
    			
    		}//if end
            
            
    	}catch(Exception e) {
    		System.out.println("아이디/비번 찾기 실패 : " + e);
    	}finally {
    		DBClose.close(con, pstmt, rs);
    	}//end
    	return flag;
    }//findID() end
    	
    

    
    
}//class end
