package fr.eni.tp.filmotheque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import fr.eni.tp.filmotheque.bll.contexte.ContexteService;
import fr.eni.tp.filmotheque.bo.Membre;

@Controller
@RequestMapping("/contexte")
@SessionAttributes({"membreEnSession"})
public class ContexteController {
	
	private ContexteService contexteService;
	
	public ContexteController(ContexteService contexteService) {
		this.contexteService = contexteService;
	}
	
	
	@GetMapping("/cloture")
	public String finSession(SessionStatus sessionStatus) {

		sessionStatus.setComplete();

		return "redirect:/films";	
	}
	
	@GetMapping("/session")
	public String connexion(@RequestParam(name = "email", required = false, defaultValue = "jtrillard@campus-eni.fr") String email, @ModelAttribute("membreEnSession") Membre membreEnSession) {
		Membre membre = this.contexteService.charger(email);
		if(membre != null) {
			membreEnSession.setId(membre.getId());
			membreEnSession.setNom(membre.getNom());
			membreEnSession.setPrenom(membre.getPrenom());
			membreEnSession.setPseudo(membre.getPseudo());
			membreEnSession.setAdmin(membre.isAdmin());
		}else {
			membreEnSession.setId(0);
			membreEnSession.setNom(null);
			membreEnSession.setPrenom(null);
			membreEnSession.setPseudo(null);
			membreEnSession.setAdmin(false);
		}
		// Evidemment on évite de mettre un mot de passe en session 
		// (surtout non chiffré)

		
		return "redirect:/films";	
	}
	
	@GetMapping
	public String choixContexte() {
		return "contexte/view-contexte";
	}
	
	@ModelAttribute("membreEnSession")
	public Membre addMembreEnSession() {
		System.out.println("Add membre en session");
		return new Membre();
	}
	

}
