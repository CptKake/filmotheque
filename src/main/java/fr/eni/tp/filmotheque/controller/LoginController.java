package fr.eni.tp.filmotheque.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.filmotheque.bll.contexte.ContexteService;
import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.dal.MembreDAO;

@Controller
@SessionAttributes({"membreEnSession"})
public class LoginController {

	private ContexteService contexteService;

	
	
	public LoginController(ContexteService contexteService) {
		this.contexteService = contexteService;
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/session")
	public String rechercherMembre(@ModelAttribute ("membreEnSession") Membre membreSession, Principal principal) {
		String email = principal.getName();
		Membre m = this.contexteService.charger(email);
		
		membreSession.setId(m.getId());
		membreSession.setNom(m.getNom());
		membreSession.setPrenom(m.getPrenom());
		membreSession.setPseudo(m.getPseudo());
		membreSession.setAdmin(m.isAdmin());
		
		System.out.println("membreEnSession =" + membreSession);
		
		return "redirect:/";	
	}
	
	@ModelAttribute("membreEnSession")
	public Membre chargerMembreEnSession() {
		
		return new Membre();
	}
}
