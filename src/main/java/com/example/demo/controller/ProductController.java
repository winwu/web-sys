package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.entity.ProductSpecs;
import com.example.demo.exception.CustomException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductSpecRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/products")
public class ProductController {
    @Autowired
    ProductRepository repository;

    @Autowired
    ProductSpecRepository productSpecRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> productList(
            @RequestParam(required = false) String search,
            @RequestParam(value = "start", defaultValue = "0") Integer start,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        try {
            List<Product> products = new ArrayList<>();
            Pageable pageable = PageRequest.of(start, limit, sort);
            Page<Product> pageResult;

            if (search == null) {
                pageResult = repository.findAll(pageable);
            } else {
                pageResult = repository.findByTitleOrSubTitleContaining(search, search, pageable);
            }

            products = pageResult.getContent();
            Map<String, Object> response = new HashMap<>();
            response.put("code", "SUCCESS");
            response.put("data", products);
            response.put("currentPage", pageResult.getNumber());
            response.put("totalRecords", pageResult.getTotalElements());
            response.put("totalPages", pageResult.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable("id") Integer id) {
        Map<String, Object> response = new HashMap<>();

        Product product = repository
                .findById(Long.valueOf(id))
                .orElseThrow(() -> new CustomException("Not found", HttpStatus.NOT_FOUND));
        response.put("code", "SUCCESS");
        response.put("data", product);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity add(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "subTitle", defaultValue = "") String subTitle,
            @RequestParam(value = "isPublished", defaultValue = "1") Integer isPublished,
            @RequestParam(value = "isFeature", defaultValue = "0") Integer isFeature,
            @RequestParam(value = "specs", required = false) String specs,
            @RequestParam(value = "bannerImg", required = false) MultipartFile bannerImg,
            @RequestParam(value = "coverImg", required = false) MultipartFile coverImg) {

        Map<String, Object> response = new HashMap<>();

        Product product = new Product();
        product.setTitle(title);
        product.setSubTitle(subTitle);
        product.setIsPublished(isPublished);
        product.setIsFeature(isFeature);

// @TODO: 檔案上傳
//        if (image != null && image.getSize() > 0) {
//            try {
//                String newFileName = fileService.save(uploadPath + File.separator + "news" + File.separator, image);
//                news.setImage(newFileName);
//            } catch (IOException e) {
//                response.put("code", "FAILURE");
//                response.put("message", e.getMessage());
//                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//            }
//        }


        product = repository.save(product);

        if (specs != null) {
            Gson gson = new Gson();
            ProductSpecs[] specsArray = gson.fromJson(specs, ProductSpecs[].class);
            for (ProductSpecs spec : specsArray) {
                spec.setProduct(product);
                System.out.println(spec);
                productSpecRepository.save(spec);
            }
        }

        response.put("code", "SUCCESS");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity update(
            @PathVariable("id") Long id,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "subTitle") String subTitle,
            @RequestParam(value = "isPublished") Integer isPublished,
            @RequestParam(value = "isFeature") Integer isFeature,
            @RequestParam(value = "specs", required = false) String specs,
            @RequestParam(value = "bannerImg", required = false) MultipartFile bannerImg,
            @RequestParam(value = "coverImg", required = false) MultipartFile coverImg,
            @RequestParam(value = "isDeleteBanner", required = false) String isDeleteBanner,
            @RequestParam(value = "isDeleteCover", required = false) String isDeleteCover
    ) {
        Map<String, Object> response = new HashMap<>();
        Product product = repository.getOne(id);

        product.setTitle(title);
        product.setSubTitle(subTitle);
        product.setIsPublished(isPublished);
        product.setIsFeature(isFeature);

        if (isDeleteBanner != null && isDeleteBanner.equals("1")) {
            product.setBannerImg(null);
        } else {
// @TODO
//            if (image != null && image.getSize() > 0) {
//                try {
//                    String newFileName = fileService.save(uploadPath + File.separator + "news" + File.separator, image);
//                    news.setImage(newFileName);
//                } catch (IOException e) {
//                    response.put("code", "FAILURE");
//                    response.put("message", e.getMessage());
//                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//                }
//            }
        }

        if (isDeleteCover != null && isDeleteCover.equals("1")) {
            product.setCoverImg(null);
        }

        try {
            repository.save(product);

            // handle specs
            if (specs == null) {
                // if specs doesn't exists, delete all
                productSpecRepository.deleteByProductId(product.getId());
            } else {
                // create or update or delete specs
                Gson gson = new Gson();
                ProductSpecs[] requestSpecs = gson.fromJson(specs, ProductSpecs[].class);
                System.out.println("requestSpecs:" + requestSpecs.toString());

                // 先處理新增的部分
                List<ProductSpecs> added = Arrays.stream(requestSpecs).filter(f -> f.getId() <= 0).collect(Collectors.toList());
                for (ProductSpecs s : added) {
                    s.setProduct(product);
                }
                productSpecRepository.saveAll(added);

                // 比較要刪除以及更新的部分
                List<ProductSpecs> existedSpecInDB = productSpecRepository.findByProductId(product.getId());
                if (existedSpecInDB.size() > 0) {
                    for (ProductSpecs s : existedSpecInDB) {
                        long currentId = s.getId();
                        ProductSpecs existsInRequest = Arrays.stream(requestSpecs).filter(f -> f.getId() == currentId).findFirst().orElse(null);
                        if (existsInRequest != null) {
                            existsInRequest.setProduct(product);
                            productSpecRepository.save(existsInRequest);
                        } else {
                            productSpecRepository.deleteById(currentId);
                        }
                    }
                }
            }
            response.put("code", "SUCCESS");
        } catch (Exception e) {
            response.put("code", "FAILURE");
            response.put("message", e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "{ids}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("ids") Long[] ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            repository.deleteByIdIn(Arrays.asList(ids));
            response.put("code", "SUCCESS");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("code", "FAILURE");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
