package com.rihab.interventions.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.rihab.interventions.entities.Image;
import com.rihab.interventions.repos.ImageRepository;

import io.jsonwebtoken.io.IOException;
import org.springframework.http.MediaType;


@Service
public class ImageServiceImpl  implements ImageService{
	@Autowired
	 ImageRepository imageRepository;
	
	
	@Override
	 public Image uplaodImage(MultipartFile file) throws IOException {
	 /*Ce code commenté est équivalent au code utilisant le design pattern Builder
	 * Image image = new Image(null, file.getOriginalFilename(),
	 file.getContentType(), file.getBytes(), null);
	 return imageRepository.save(image);*/
	 try {
		return imageRepository.save(Image.builder()
		 .name(file.getOriginalFilename())
		 .type(file.getContentType())
		 .image(file.getBytes()).build() );
	} catch (java.io.IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	 }
	 
	 @Override
	 public Image getImageDetails(Long id) throws IOException{
	 final Optional<Image> dbImage = imageRepository. findById (id);
	 return Image.builder()
	 .idImage(dbImage.get().getIdImage())
	 .name(dbImage.get().getName())
	 .type(dbImage.get().getType())
	 .image(dbImage.get().getImage()).build() ;
	 }
	 @Override
	 public ResponseEntity<byte[]> getImage(Long id) throws IOException{
	 final Optional<Image> dbImage = imageRepository. findById (id);
	 return ResponseEntity
	 .ok()
	 .contentType(MediaType.valueOf(dbImage.get().getType()))
	 .body(dbImage.get().getImage());
	 }
	 
	 @Override
	 public void deleteImage(Long id) {
	 imageRepository.deleteById(id);
	 }
	
}
