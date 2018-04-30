package user;

import net.sf.json.JSONArray;
import org.hibernate.Session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//login[[key,name,tags,frequency,date,ownerid]]
//not login[]
@WebServlet("/QueryServlet")
public class QueryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryServlet(){
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Begin unit of work
            Session session=HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            response.setHeader("Content-Type", "text/html;charset=utf-8");
            Object o=request.getSession().getAttribute("userid");
            int owner;
            if (o==null){owner=-1;}
            else {owner=Integer.parseInt(o.toString());}
            // Write HTML header

            PrintWriter out = response.getWriter();
           // String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数

            if (owner==-1){
               // out.println(jsonpCallback+"({'question':[{'login':0}]})");
                ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
                ArrayList<String> ur=new ArrayList<String>();
                ur.add("-1");
                qJ.add(JSONArray.fromObject(ur));
                out.println(JSONArray.fromObject(qJ));
            }
            else {
                printLibrary(out, owner);
            }
            out.flush();
            out.close();
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
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


    @SuppressWarnings({ "unchecked" })
    private void printLibrary(PrintWriter out,int userid){
        List<UQ_Library> res=HibernateUtil.getSessionFactory().getCurrentSession()
                .createQuery("from UQ_Library l where l.ulKey.userId=?").setParameter(0,userid).list();


            Iterator it = res.iterator();
            ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
            while (it.hasNext()) {
                UQ_Library lib = (UQ_Library) it.next();
                ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.add(String.valueOf(lib.getUlKey().getLibraryId()).trim());
                arrayList.add(lib.getQuestion().getName());
                arrayList.add(lib.getTagOne()+" "+lib.getTagTwo());
                arrayList.add(String.valueOf(lib.getFrequency()));
                arrayList.add(String.valueOf(lib.getDate()));
                arrayList.add(String.valueOf(lib.getQuestion().getOwner().getId()));
                qJ.add(JSONArray.fromObject(arrayList));
               /* j=j+",{'name':'"+lib.getQuestion().getName()
                        +"','tag':['"+lib.getTagOne()+"','"+lib.getTagTwo()+"']"
                        +",'frequent':"+lib.getFrequency()
                        +",'date':'"+lib.getDate()
                        +"','key':"+lib.getUlKey().getLibraryId()
                        +",'content':'"+lib.getQuestion().getContent()
                        +"'}";*/
            }
            JSONArray q=JSONArray.fromObject(qJ.toArray());
            out.println(q);
           // j+="]})";
           // out.println(j);


    }



}
