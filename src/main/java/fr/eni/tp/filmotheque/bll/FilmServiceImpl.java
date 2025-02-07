package fr.eni.tp.filmotheque.bll;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Genre;
import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.bo.Participant;
import fr.eni.tp.filmotheque.dal.AvisDAO;
import fr.eni.tp.filmotheque.dal.FilmDAO;
import fr.eni.tp.filmotheque.dal.GenreDAO;
import fr.eni.tp.filmotheque.dal.MembreDAO;
import fr.eni.tp.filmotheque.dal.ParticipantDAO;
import fr.eni.tp.filmotheque.exception.BusinessException;

@Service
@Primary
public class FilmServiceImpl implements FilmService {

	private FilmDAO filmDAO;
	private GenreDAO genreDAO;
	private ParticipantDAO participantDAO;
	private AvisDAO avisDAO;
	private MembreDAO membreDAO;
	
	public FilmServiceImpl(FilmDAO filmDAO, GenreDAO genreDAO, ParticipantDAO participantDAO, AvisDAO avisDAO, MembreDAO membreDAO) {
		this.filmDAO = filmDAO;
		this.genreDAO = genreDAO;
		this.participantDAO = participantDAO;
		this.avisDAO = avisDAO;
		this.membreDAO = membreDAO;
	}

	@Override
	public List<Film> consulterFilms() {
		List<Film> listeFilms = filmDAO.findAll();
		for (Film film : listeFilms) {
			Genre genre = this.genreDAO.read(film.getGenre().getId());
			film.setGenre(genre);
			Participant realisateur = this.participantDAO.read(film.getRealisateur().getId());
			film.setRealisateur(realisateur);
		}
		
		return listeFilms;
	}

	@Override
	public Film consulterFilmParId(long id) {
		
		Film film = this.filmDAO.read(id);
		Genre genre = this.genreDAO.read(film.getGenre().getId());
		film.setGenre(genre);
		Participant realisateur = this.participantDAO.read(film.getRealisateur().getId());
		film.setRealisateur(realisateur);
		
		List<Participant> acteurs = this.participantDAO.findActeurs(id);
		film.setActeurs(acteurs);
		
		film.setAvis(consulterAvis(id));
		
		return film;
	}

	@Override
	public List<Genre> consulterGenres() {
		return this.genreDAO.findAll();
	}

	@Override
	public List<Participant> consulterParticipants() {
		return this.participantDAO.findAll();
	}

	@Override
	public Genre consulterGenreParId(long id) {
		return this.genreDAO.read(id);
	}

	@Override
	public Participant consulterParticipantParId(long id) {
		return this.participantDAO.read(id);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void creerFilm(Film film) throws BusinessException {
		// permet de simuler un film ayant un genre inexistant 
		/*film.getGenre().setId(20L);
		film.getActeurs().forEach(a->a.setId(0));
		film.getRealisateur().setId(0);*/
		
		BusinessException be = new BusinessException();
		
		
		
		boolean valider = validerGenre(film.getGenre(), be);
		valider &= validerActeurs(film.getActeurs(), be);
		valider &= validerRealisateur(film.getRealisateur(), be);
		valider &= validerTitreUnique(film, be);
		
		if(valider) {
			this.filmDAO.create(film);
			
			film.getActeurs().forEach(a->this.participantDAO.createActeur(a.getId(), film.getId()));
		}else {
			
			throw be;
		}
		
		
		
	}

	

	@Override
	public void publierAvis(Avis avis, long idFilm) {
		this.avisDAO.create(avis, idFilm);
		
	}

	@Override
	public List<Avis> consulterAvis(long idFilm) {
		List<Avis> avis = this.avisDAO.findByFilm(idFilm);
		if(avis != null) {
			// Association avec le membre
			avis.forEach(a -> {
				Membre membre = membreDAO.read(a.getMembre().getId());
				a.setMembre(membre);
			});
		}
		return avis;
	}
	
	private boolean validerGenre(Genre genre, BusinessException be) {
		boolean valide = true;
		
		try {
			Genre genreTrouve = this.genreDAO.read(genre.getId());
			
			if(genreTrouve == null) {
				valide = false;
				be.addCleErreur("validation.film.genre.id.inconnu");
			}
		
		}catch(EmptyResultDataAccessException e ) {
			valide=false;
			be.addCleErreur("validation.film.genre.id.inconnu");
		}
		
		return valide;
	}
	
	
	private boolean validerRealisateur(Participant realisateur, BusinessException be) {
		boolean valide = true;
		
		List<Participant> participants = List.of(realisateur);
		
		try {
			List<Participant> listeParticipant = this.participantDAO.findParticipants(participants);
			
			if(listeParticipant != null && listeParticipant.size() != 1) {
				valide = false;
				be.addCleErreur("validation.film.realisateur.id.inconnu");
			}
		}catch(EmptyResultDataAccessException e ) {
			valide=false;
			be.addCleErreur("validation.film.realisateur.id.inconnu");
		}
		
		
		return valide;
		
	}
	
	private boolean validerActeurs(List<Participant> listeActeurs, BusinessException be) {
		boolean valide = true;
		
		try {
			
			if(listeActeurs!=null && !listeActeurs.isEmpty()) {
				List<Participant> listeParticipant = this.participantDAO.findParticipants(listeActeurs);
				
				if(listeParticipant != null && listeParticipant.size() != listeActeurs.size()) {
					valide = false;
					be.addCleErreur("validation.film.acteur.id.inconnu");
				}
			}
			
		}catch(EmptyResultDataAccessException e ) {
			valide=false;
			be.addCleErreur("validation.film.acteur.id.inconnu");
		}
		
		
		return valide;
		
	}
	
	private boolean validerTitreUnique(Film film, BusinessException be) {
		boolean valide = true;
		
		int nbTitre = this.filmDAO.countTitre(film.getTitre());
		
		if(nbTitre != 0) {
			valide = false;
			be.addCleErreur("validation.film.unique");
		}
			
		
		return valide;
		
	}
	
	
	private String hello(String film) {
		System.out.println("Hello Alexis");
		return film;
	}
	
	
	
	
	

}
