package com.rihab.interventions.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.rihab.interventions.dto.ArticleDTO;
import com.rihab.interventions.dto.PieceRequestDTO;
import com.rihab.interventions.dto.TicketDTO;
import com.rihab.interventions.dto.UserDTO;
import com.rihab.interventions.entities.Article;
import com.rihab.interventions.entities.Demandeur;
import com.rihab.interventions.entities.Intervention;
import com.rihab.interventions.entities.PieceRechange;
import com.rihab.interventions.entities.PieceRechangeId;
import com.rihab.interventions.entities.PieceRechangeRequest;
import com.rihab.interventions.entities.Technicien;
import com.rihab.interventions.entities.Ticket;
import com.rihab.interventions.repos.ArticleRepository;
import com.rihab.interventions.repos.PieceRechangeRepository;
import com.rihab.interventions.repos.PieceRequestRepository;
import com.rihab.interventions.repos.TicketRepository;

@Service
public class PieceRequestServiceImpl implements PieceRequestService {
	
	@Autowired
	PieceRequestRepository pieceRequestRepository;
	@Autowired
	TicketRepository ticketRepository;
	@Autowired
	TechnicienService technicienService;
	@Autowired
	PieceRechangeRepository pieceRechangeRepository;
	@Autowired
	ArticleRepository articleRepository;
	
	
	public PieceRechangeRequest toPieceRequest(PieceRequestDTO request, Ticket ticket) {
	    Article article = toArticle(request.getArticle());
	    return PieceRechangeRequest.builder()
	            .ticket(ticket)
	            .codeDemande(request.getCodeDemande())
	            .statutDemande(request.getStatutDemande())
	            .quantiteDemande(request.getQuantiteDemande())
	            .etat(request.getEtat())
	            .autreArt(request.getAutreArt())
	            .quantitePieceRechange(request.getQuantitePieceRechange())
	            .done(request.getDone())
	            .distingtion(request.getDistingtion())
	            .article(article)
	            .build();
	}

	public List<PieceRechangeRequest> toPieceRequests(List<PieceRequestDTO> requests) {
	    List<PieceRechangeRequest> pieceRequests = new ArrayList<>();
	    if (!requests.isEmpty()) {
	        Ticket ticket = ticketRepository.findById(requests.get(0).getTicket().getInterCode())
	                                        .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé"));
	        for (PieceRequestDTO request : requests) {
	            pieceRequests.add(toPieceRequest(request, ticket));
	        }
	    }
	    return pieceRequests;
	}

	public PieceRequestDTO toPieceRequestDTO(PieceRechangeRequest request) {
	    ArticleDTO articleDTO = request.getArticle() != null ? toArticleDTO(request.getArticle()) : null;
	    return PieceRequestDTO.builder()
	            .codeDemande(request.getCodeDemande())
	            .statutDemande(request.getStatutDemande())
	            .quantiteDemande(request.getQuantiteDemande())
	            .etat(request.getEtat())
	            .autreArt(request.getAutreArt())
	            .quantitePieceRechange(request.getQuantitePieceRechange())
	            .done(request.getDone())
	            .distingtion(request.getDistingtion())
	            .ticket(toTicketDTO(request.getTicket()))
	            .article(articleDTO)
	            .build();
	}

	    

		public TicketDTO toTicketDTO(Ticket request) {
		    TicketDTO.TicketDTOBuilder builder = TicketDTO.builder()
		          
		    		.interCode(request.getInterCode())
		            .interDesignation(request.getInterDesignation())
		            .interPriorite(request.getInterPriorite())
		            .interStatut(request.getInterStatut())
		            .machineArret(request.getMachineArret())
		            .dateArret(request.getDateArret())
		            .dureeArret(request.getDureeArret())
		            .dateCreation(request.getDateCreation())
		            .datePrevue(request.getDatePrevue())
		            .description(request.getDescription())
		            .sousContrat(request.getSousContrat())
		            .sousGarantie(request.getSousGarantie())
		           
		            .intervention(request.getIntervention())
		            .equipement(request.getEquipement())
		            .demandeur(request.getDemandeur())
		            .technicien(request.getTechnicien())
		            .interventionNature(request.getInterventionNature());
		    // Map other fields if needed
		            
		    return builder.build();
		} 
	    
		public Article toArticle(ArticleDTO articleDTO) {
		    return Article.builder()
		            .codeArticle(articleDTO.getCodeArticle())
		            .nomArticle(articleDTO.getNomArticle())
		            .marqueArticle(articleDTO.getMarqueArticle())
		            .qteArticle(articleDTO.getQteArticle())
		            .build();
		}
		public ArticleDTO toArticleDTO(Article article) {
		    return ArticleDTO.builder()
		            .codeArticle(article.getCodeArticle())
		            .nomArticle(article.getNomArticle())
		            .marqueArticle(article.getMarqueArticle())
		            .qteArticle(article.getQteArticle())
		            .build();
		}

	    
	    
		public List<PieceRequestDTO> savePieceRequests(List<PieceRequestDTO> pieceRequests) {
		    List<PieceRequestDTO> savedPieceRequests = new ArrayList<>();

		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    if (authentication == null || !authentication.isAuthenticated()) {
		        throw new RuntimeException("User not authenticated");
		    }
		    if (!(authentication.getPrincipal() instanceof UserDetails)) {
		        throw new RuntimeException("User not authenticated as UserDetails");
		    }
		    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		    String username = userDetails.getUsername();
		    Technicien technicien = technicienService.getTechnicienByUsername(username);
		    if (technicien == null) {
		        throw new RuntimeException("Technician not found");
		    }

		    List<PieceRechangeRequest> pieceRechangeRequests = toPieceRequests(pieceRequests);
		    for (PieceRechangeRequest pieceRechangeRequest : pieceRechangeRequests) {
		        PieceRechangeRequest savedPieceRequest = pieceRequestRepository.save(pieceRechangeRequest);
		        savedPieceRequests.add(toPieceRequestDTO(savedPieceRequest));
		    }
		    return savedPieceRequests;
		}
	
	@Override
	public void deletePieceRequest(PieceRechangeRequest inter) {
		pieceRequestRepository.delete(inter);
	}


	@Override
	public void deletePieceRequestByCodeDemande(long code) {
		pieceRequestRepository.deleteById(code);
	}


	@Override
	public PieceRequestDTO getPieceRequest(long code) {
		return toPieceRequestDTO(pieceRequestRepository.findById(code).get());
	}


	@Override
	public List<PieceRequestDTO> getAllPiecesRequests1() {
	return pieceRequestRepository.findAll().stream()
			.map(this::toPieceRequestDTO)
			.collect(Collectors.toList());
	}

	@Override
	public List<PieceRechangeRequest> getAllPiecesRequests() {
		
		 return pieceRequestRepository.findAll();
	}

	@Override
	public PieceRequestDTO updateEtatPieceRequest(Long codeDemande) {
	    PieceRechangeRequest pieceRechangeRequest = pieceRequestRepository.findById(codeDemande)
	        .orElseThrow(() -> new EntityNotFoundException("Piece request with code " + codeDemande + " not found"));

	    // Update only the `etat` field
	    pieceRechangeRequest.setEtat("Ancien");

	    PieceRechangeRequest updatedPieceRequest = pieceRequestRepository.save(pieceRechangeRequest);
	    return toPieceRequestDTO(updatedPieceRequest);
	}

	@Override
	public PieceRequestDTO updateStatutDemandePieceRequest(String interCode, String newStatutDemande) {
	    List<PieceRechangeRequest> pieceRechangeRequests = pieceRequestRepository.findByTicketInterCode(interCode);
	    if (pieceRechangeRequests.isEmpty()) {
	        throw new EntityNotFoundException("No piece request found for ticket with interCode " + interCode);
	    }

	    for (PieceRechangeRequest pieceRechangeRequest : pieceRechangeRequests) {
	        // Update the `statutDemande` field for each piece request
	        pieceRechangeRequest.setStatutDemande(newStatutDemande);
	        pieceRequestRepository.save(pieceRechangeRequest);
	    }

	    // Assuming all pieces for the ticket have the same `interCode`, we return the first one
	    return toPieceRequestDTO(pieceRechangeRequests.get(0));
	}

	
	

}
