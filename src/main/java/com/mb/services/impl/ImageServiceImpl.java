package com.mb.services.impl;

import java.io.IOException;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.mb.helpers.AppConstants;
import com.mb.services.ImageService;

@Service
public class ImageServiceImpl implements ImageService {

	private Cloudinary cloudinary;

	public ImageServiceImpl(Cloudinary cloudinary) {
		this.cloudinary = cloudinary;
	}

//	@Override
//	public String uploadImage(MultipartFile userImage, String filename) {
//
//		// Write Code here... To do Upload Image-MultipartFile & do Return Image-MultipartFile URL
//		try {
//			byte[] data = new byte[userImage.getInputStream().available()];
//			userImage.getInputStream().read(data);
//			cloudinary.uploader().upload(data, ObjectUtils.asMap("public_id", filename));
//
//			return this.getUrlFromPublicId(filename);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	// @Override
//	public List<String> uploadImages(List<MultipartFile> userImages, String filename) {
//	    List<String> imageUrls = new ArrayList<>();
//	    for (MultipartFile userImage : userImages) {
//	        try {
//	            byte[] data = new byte[userImage.getInputStream().available()];
//	            userImage.getInputStream().read(data);
//	            cloudinary.uploader().upload(data, ObjectUtils.asMap("public_id", filename));
//	            String imageUrl = this.getUrlFromPublicId(filename);
//	            imageUrls.add(imageUrl);
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//	    return imageUrls;
//	}
	@Override
	public List<String> uploadImages(List<MultipartFile> userImages, String filename) {
		List<String> imageUrls = new ArrayList<>();
		List<String> publicIds = new ArrayList<>();

		for (MultipartFile userImage : userImages) {
			String publicId = UUID.randomUUID().toString();
			try {
				byte[] data = new byte[userImage.getInputStream().available()];
				userImage.getInputStream().read(data);
				cloudinary.uploader().upload(data, ObjectUtils.asMap("public_id", publicId));
				String imageUrl = this.getUrlFromPublicId(publicId);
				imageUrls.add(imageUrl);
				publicIds.add(publicId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return imageUrls;
	}

	@Override
	public String getUrlFromPublicId(String publicId) {
		return cloudinary.url()
				.transformation(new Transformation<>().width(AppConstants.CONTENT_IMAGE_WIDTH)
						.height(AppConstants.CONTENT_IMAGE_HEIGHT).crop(AppConstants.CONTENT_IMAGE_CROP))
				.generate(publicId);
	}
}