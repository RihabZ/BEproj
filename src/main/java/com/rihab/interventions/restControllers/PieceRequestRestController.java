package com.rihab.interventions.restControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rihab.interventions.dto.PieceRequestDTO;
import com.rihab.interventions.entities.PieceRechangeRequest;

import com.rihab.interventions.service.PieceRequestService;


@RestController
@RequestMapping("/api")
@CrossOrigin
public class PieceRequestRestController {

	

	@Autowired
	PieceRequestService pieceReqService;
	

	@PreAuthorize("hasAuthority('MANAGER')")
@RequestMapping(path = "allRequests", method = RequestMethod.GET)
public List<PieceRechangeRequest> getAllRequests() {
    return pieceReqService.getAllPiecesRequests();
}


	@PreAuthorize("hasAuthority('MANAGER')")
@RequestMapping(value="/getByCodeDemande/{codeDemande}",method = RequestMethod.GET)
public PieceRequestDTO getPieceRequestById(@PathVariable("codeDemande") long codeDemande) {
	return pieceReqService.getPieceRequest(codeDemande);
 }


@PreAuthorize("hasAuthority('TECHNICIEN')")
@RequestMapping(path = "/addPieceRequest", method = RequestMethod.POST)
public List<PieceRequestDTO> createPieceRequests(@RequestBody List<PieceRequestDTO> pieceReqDTOs) {
    return pieceReqService.savePieceRequests(pieceReqDTOs);
}
@PreAuthorize("hasAuthority('TECHNICIEN')")
@RequestMapping(value="/deletePieceRequest/{codeDemande}",method = RequestMethod.DELETE)

public void deletePieceRequest(@PathVariable("codeDemande") long codeDemande)
{
	pieceReqService.deletePieceRequestByCodeDemande(codeDemande);
}

@PreAuthorize("hasAuthority('TECHNICIEN')")
@PutMapping("/updateEtatPieceRequest/{codeDemande}")
public ResponseEntity<PieceRequestDTO> updateEtatPieceRequest(@PathVariable Long codeDemande) {
    PieceRequestDTO updatedPieceRequest = pieceReqService.updateEtatPieceRequest(codeDemande);
    return ResponseEntity.ok(updatedPieceRequest);
}

@PreAuthorize("hasAuthority('TECHNICIEN')")
@RequestMapping(path = "/updateStatutDemande/{interCode}", method = RequestMethod.PUT)
public PieceRequestDTO updateStatutDemande(@PathVariable String interCode, @RequestParam String newStatutDemande) {
    return pieceReqService.updateStatutDemandePieceRequest(interCode, newStatutDemande);
}


}
