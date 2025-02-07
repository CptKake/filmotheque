package fr.eni.tp.filmotheque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.filmotheque.bll.FilmService;
import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Membre;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/avis")
@SessionAttributes({"membreEnSession"})
public class AvisController {
	
	private FilmService filmService;
	
	public AvisController(FilmService filmService) {
		this.filmService = filmService;
	}
	
	// Création d'un nouvel avis
	@GetMapping("/creer")
	public String creerAvis(@RequestParam("idFilm") long idFilm, Model model,
	@ModelAttribute("membreEnSession") Membre membreEnSession) {
		if (membreEnSession != null && membreEnSession.getId() >= 1) {
			// Il y a un membre en session
			Film f = this.filmService.consulterFilmParId(idFilm);
			
			if(f != null) {
				model.addAttribute("film", f);
				
				Avis avis = new Avis();
				model.addAttribute(avis);
				return "view-avis-creer";
			}
		}	
		return "redirect:/films";
			
	}
	
	
	@PostMapping("/creer")
	public String creerAvis(@ModelAttribute(name = "membreEnSession") Membre membreEnSession, @Valid @ModelAttribute(name = "avis") Avis avis, BindingResult bindingResult ,Model model, @RequestParam(name = "idFilm") long idFilm) {
		if (bindingResult.hasErrors()) {
			Film film = this.filmService.consulterFilmParId(idFilm);
			
			model.addAttribute("film",film );
			return "view-avis-creer";
		}else {
			
		}
		if (membreEnSession != null && membreEnSession.getId() >= 1) {
			// Il y a un membre en session
			avis.setMembre(membreEnSession);
			System.out.println(avis);
			// Sauvegarde de l’avis avec l’identifiant du film :
			filmService.publierAvis(avis, idFilm);
		}
		// Redirection vers la liste des films :
		return "redirect:/films";

	}
	

}
