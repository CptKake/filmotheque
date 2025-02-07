package fr.eni.tp.filmotheque.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.filmotheque.bll.FilmService;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Genre;
import fr.eni.tp.filmotheque.bo.Participant;
import fr.eni.tp.filmotheque.exception.BusinessException;
import jakarta.validation.Valid;

@Controller
@SessionAttributes({"genresSession", "participantsSession", "membreEnSession"})
public class FilmController {
	
	private FilmService filmService;
	

	public FilmController(FilmService filmService) {
		this.filmService = filmService;
	}


	@GetMapping("/films/detail")
	public String afficherUnFilm(@RequestParam("id") long id, Model model) {
		Film film = this.filmService.consulterFilmParId(id);
		model.addAttribute("film", film);
		
		return "view-film-detail";
	}



	@GetMapping("/films")
	public String afficherFilms(Model model) {
		List<Film> films = this.filmService.consulterFilms();
		
		model.addAttribute("films", films);
		
		return "view-films";
	}
	
	
	@GetMapping("/films/creer")
	public String afficherCreationFilm(Model model) {
		
		model.addAttribute("film", new Film());
		
		return "view-film-creation";
	}
	
	@PostMapping("/films/creer")
	public String creerFilm(@Valid @ModelAttribute("film") Film film, BindingResult bindingResult) {
		System.out.println("film = " + film);
		
		
		if(bindingResult.hasErrors()) {
			return "view-film-creation"; 
		}else {
			try {
				this.filmService.creerFilm(film);
				return "redirect:/films";
			} catch (BusinessException e) {
				e.printStackTrace();
				e.getClesErreurs().forEach(cle->{
					ObjectError erreur = new ObjectError("globalError", cle);
					bindingResult.addError(erreur);
				});
				return "view-film-creation"; 
			}
			
			
		}
		
	}
	
	
	@ModelAttribute("genresSession")
	public List<Genre> chargerGenresSession(){
		System.out.println("Chargement des genres en session");
		return this.filmService.consulterGenres();
	}
	
	@ModelAttribute("participantsSession")
	public List<Participant> chargerParticipantsSession(){
		System.out.println("Chargement des participants en session");
		return this.filmService.consulterParticipants();
	}
	
	

}
