package ProgettoLibri_SB.Interceptor;

import ProgettoLibri_SB.Entity.TipoUtenza;
import ProgettoLibri_SB.Entity.Utente;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Component
public class LoggingInterceptor implements HandlerInterceptor {


    //viene chiamato prima che il controller inizi a lavorare
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {

        String uri = request.getRequestURI();

        if (uri.equals("/error")) {
            return true;
        }

        Utente utente = (Utente) request.getSession().getAttribute("utentecorrente");

        if (utente == null) {
            if(uri.contains("/account/registraAdmin") || uri.contains("/account/registraLettore")){
                response.setStatus(HttpServletResponse.SC_OK);
                return true;
            }else if(uri.contains("/account/accesso")){
                response.setStatus(HttpServletResponse.SC_OK);
                return true;
            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Devi prima effettuare il login.");
                return false;
            }
        }

        TipoUtenza tipo = utente.getTipo_utenza();

        if (uri.contains("/utente") && (tipo.equals(TipoUtenza.LETTORE) || tipo.equals(TipoUtenza.AMMINISTRATORE))) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        if (uri.contains("/utente/admin") && tipo.equals(TipoUtenza.LETTORE)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Accesso negato: area riservata agli amministratori.");
            return false;
        }

        if (uri.contains("/utente/lettore") && tipo.equals(TipoUtenza.AMMINISTRATORE)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Accesso negato: area riservata ai lettori.");
            return false;
        }

        return true;
    }


    //dopo che gestisco la chiamata api
    public void postHandle(HttpServletRequest request, HttpServletResponse response,Object handler,@Nullable ModelAndView modelAndView) throws java.lang.Exception {

    }

    //dopo la richiesta api
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler, @Nullable Exception ex) throws java.lang.Exception {

    }


}
