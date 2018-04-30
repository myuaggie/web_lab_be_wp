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


@WebServlet("/QueryCommonServlet")
public class QueryCommonServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryCommonServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @SuppressWarnings({ "unchecked" })
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Session session=HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            response.setHeader("Content-Type", "text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            List<UQ_Library> res=HibernateUtil.getSessionFactory().getCurrentSession()
                    .createQuery("from UQ_Library l where l.ulKey.userId=?").setParameter(0,111111).list();
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
                arrayList.add(String.valueOf(lib.getQuestion().getOwner().getUsername()));
                arrayList.add(String.valueOf(lib.getQuestion().getContent()));
                arrayList.add(String.valueOf(lib.getQuestion().getReference()));
                qJ.add(JSONArray.fromObject(arrayList));
            }
            JSONArray q=JSONArray.fromObject(qJ.toArray());
            out.println(q);
            out.flush();
            out.close();
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
        }
        catch (Exception ex) {
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
            if (ServletException.class.isInstance(ex)) {
                throw (ServletException) ex;
            } else {
                throw new ServletException(ex);
            }
        }
    }
}