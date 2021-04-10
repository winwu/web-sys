package com.example.demo.service.impl;

import com.example.demo.repository.ProductSpecRepository;
import com.example.demo.service.ProductSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductSpecServiceImpl implements ProductSpecService {
    @Autowired
    ProductSpecRepository productSpecRepository;
}
