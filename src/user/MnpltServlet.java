package user;


import org.hibernate.Session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


// MnpltServlet?add=1/0&delete=1/0&update=1/0&key=&name=&content=&reference=&tagOne=&tagTwo=&frequency=1/0&date=1/0
@WebServlet("/MnpltServlet")
public class MnpltServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MnpltServlet(){
        super();
    }

    @SuppressWarnings({ "unchecked" })
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Begin unit of work
            Session session=HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            response.setHeader("Content-Type", "text/html;charset=utf-8");
            int owner=Integer.parseInt(request.getSession().getAttribute("userid").toString());
            PrintWriter out = response.getWriter();
            if (owner==-1){
                String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数
                out.println(jsonpCallback+"({'login':'0'})");
            }else {
                List<User> res = HibernateUtil.getSessionFactory().getCurrentSession()
                        .createQuery("from User u where u.id=?").setParameter(0, owner).list();
                Iterator it = res.iterator();
                User u = (User) it.next();
                if (request.getParameter("add").equals("1")) {
                    addSQL(request.getParameter("name"), request.getParameter("content"),
                            request.getParameter("tagOne"), request.getParameter("tagTwo"), u,
                            request.getParameter("key"), session);
                } else if (request.getParameter("delete").equals("1")) {
                    deleteSQL(request.getParameter("key"), u, session);
                } else if (request.getParameter("update").equals("1")) {
                    if (!request.getParameter("name").equals("")) {
                        updateSQL(request.getParameter("key"), u, "name", request.getParameter("name"), session);
                    } else if (!request.getParameter("content").equals("")) {
                        updateSQL(request.getParameter("key"), u, "content", request.getParameter("content"), session);
                    } else if (!request.getParameter("reference").equals("")) {
                        updateSQL(request.getParameter("key"), u, "reference", request.getParameter("reference"), session);
                    } else if (!request.getParameter("tagOne").equals("")) {
                        updateSQL(request.getParameter("key"), u, "tagOne", request.getParameter("tagOne"), session);
                    } else if (!request.getParameter("tagTwo").equals("")) {
                        updateSQL(request.getParameter("key"), u, "tagTwo", request.getParameter("tagTwo"), session);
                    } else if (request.getParameter("date").equals("1")) {
                        updateSQL(request.getParameter("key"), u, "date", "1", session);
                    } else if (request.getParameter("frequency").equals("1")) {
                        updateSQL(request.getParameter("key"), u, "frequency", "1", session);
                    }
                }
                //String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数
                //out.println(jsonpCallback+"({'login':'1'})");
            }
            out.flush();
            out.close();
           /* response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();

            writer.flush();
            writer.close();*/
            //HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
            session.getTransaction().commit();
        }
        catch (Exception ex) {
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
            if ( ServletException.class.isInstance( ex ) ) {
                throw ( ServletException ) ex;
            }
            else {
                throw new ServletException( ex );
            }
        }
    }

    //根据所给信息插入question与library   Question（question_id(auto),name,content,reference,owner)
    //                                 Library (user_id,key,question_id,tagone,tagtwo,date,frequency)
    private void addSQL(String name, String content, String tagOne, String tagTwo, User owner, String key, Session session){
       // Session session = HibernateUtil.getSessionFactory().getCurrentSession();
       // session.beginTransaction();

        Question q=new Question();
        q.setName(name);
        q.setContent(content);
        q.setOwner(owner);
        session.save(q);

        UQ_Library l=new UQ_Library();
        ULKey k=new ULKey();
        k.setLibraryId(Integer.valueOf(key));
        k.setUserId(owner.getId());
        l.setUlKey(k);
        l.setQuestion(q);
        l.setTagOne(tagOne);
        l.setTagTwo(tagTwo);
        l.setFrequency(0);
        l.setDate(new Date());
        session.save(l);

      //  session.getTransaction().commit();
    }


    //首先删除library中对应的tuple
    //根据主键找library并拿出question的id，根据id找到question的owner，如果是该用户，那么对应的question中的tuple也要删除
    @SuppressWarnings({ "unchecked" })
    private void deleteSQL(String key, User owner, Session session){
        //Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        //session.beginTransaction();

        List<UQ_Library> res=session.createQuery("from UQ_Library l where l.ulKey.userId=? and l.ulKey.libraryId=?")
                .setParameter(0,owner.getId()).setParameter(1,Integer.valueOf(key)).list();
        Iterator it = res.iterator();
        UQ_Library target=(UQ_Library) it.next();

        if (target.getQuestion().getOwner().getId()==owner.getId()){
            session.delete(target.getQuestion());
        }
        else {

            session.delete(target);
        }
       // session.getTransaction().commit();
    }
//http://localhost:8080/MnpltServlet?add=0&delete=0&update=1&key=1&name=qq1&content=&reference=&tagOne=&tagTwo=&frequency=0&date=0
    //不能够手动更新date,frequency,owner
    @SuppressWarnings({ "unchecked" })
    private void updateSQL(String key, User owner, String ctg, String var, Session session){
       // Session session = HibernateUtil.getSessionFactory().getCurrentSession();
       // session.beginTransaction();

        List<UQ_Library> res=session.createQuery("from UQ_Library l where l.ulKey.userId=? and l.ulKey.libraryId=?")
                .setParameter(0,owner.getId()).setParameter(1,Integer.valueOf(key)).list();
        Iterator it = res.iterator();
        UQ_Library target=(UQ_Library) it.next();
        Question q=target.getQuestion();
        if (ctg.equals("name")){
            q.setName(var);
            session.update(q);
        }
        else if (ctg.equals("content")){
            q.setContent(var);
            session.update(q);
        }
        else if (ctg.equals("reference")){
            q.setReference(var);
            session.update(q);
        }
        else if (ctg.equals("tagOne")){
            target.setTagOne(var);
            session.update(target);
        }
        else if (ctg.equals("tagTwo")){
            target.setTagTwo(var);
            session.update(target);
        }
        else if (ctg.equals("frequency")){
            target.setFrequency(target.getFrequency()+1);
            session.update(target);
        }
        else if (ctg.equals("date")){
            target.setDate(new Date());
            session.update(target);
        }
      //  session.getTransaction().commit();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
