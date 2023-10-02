package dev.naman.productservice.services;

import dev.naman.productservice.dtos.GenericProductDto;
import dev.naman.productservice.dtos.ProductDto;
import dev.naman.productservice.models.Category;
import dev.naman.productservice.models.Price;
import dev.naman.productservice.models.Product;
import dev.naman.productservice.repositories.CategoryRepository;
import dev.naman.productservice.repositories.ProductRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Primary
@Service("selfProductServiceImpl")
public class SelfProductServiceImpl implements ProductServiceImpl {
    private ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public SelfProductServiceImpl(ProductRepository productRepository,CategoryRepository categoryRepository1) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository1;
    }

    private GenericProductDto convertProductToProductDto(Product product) {
        GenericProductDto productDto = new GenericProductDto();
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setImage(productDto.getImage());

        return productDto;
    }
    @Override
    public GenericProductDto getProductById(UUID id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(this::convertProductToProductDto).orElse(null);
    }

    @Override
    public GenericProductDto createProduct(GenericProductDto product) {
        Category category = new Category();
        category.setName(product.getCategory());

        Price price = new Price();
        price.setPrice(product.getPrice());

        Product newProduct = new Product();
        newProduct.setTitle(product.getTitle());
        newProduct.setDescription(product.getDescription());
        newProduct.setImage(product.getImage());
        newProduct.setCategory(category);
        newProduct.setPrice(price);

        productRepository.save(newProduct);

        return convertProductToProductDto(newProduct);
    }

    @Override
    public List<GenericProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll().stream().collect(Collectors.toList());
        List<GenericProductDto> genericProductDtos = new ArrayList<>();

        for(Product product : products) {
            genericProductDtos.add(convertProductToProductDto(product));
        }

        return genericProductDtos;
    }

    @Override
    public GenericProductDto deleteProductById(UUID id) {
        
        Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty()) {
            return null;
        }
        productRepository.deleteById(id);

        return convertProductToProductDto(product.get());
    }


    public GenericProductDto updateProductById(GenericProductDto genericProductDto, UUID id) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty()) {
            return null;
        }
        Product updateProduct = new Product();
        updateProduct.setTitle(genericProductDto.getTitle());
        updateProduct.setDescription(genericProductDto.getDescription());
        updateProduct.setImage(genericProductDto.getImage());

        productRepository.save(updateProduct);
        return convertProductToProductDto(updateProduct);
    }

    @Override
    public List<ProductDto> getCategoryById(String categoryName) {
        Optional<Category> category = categoryRepository.findById(UUID.fromString(categoryName));
        if(category.isEmpty()) {
            return null;
        }
        Category category1 = category.get();
        List<ProductDto> productDtos = new ArrayList<>();

        for(Product product : category1.getProducts()) {
            ProductDto productDto = new ProductDto();
            productDto.setTitle(product.getTitle());
            productDto.setDescription(product.getDescription());
            productDto.setImage(product.getImage());
        }
        return productDtos;
    }

    @Override
    public List<String> getAllCategories() {
        List<Category> categories = categoryRepository.findAll().stream().collect(Collectors.toList());
        List<Product> products = productRepository.findAllByCategoryIn(categories);
        List<String> titles = new ArrayList<>();

        for(Product product: products) {
            titles.add(product.getTitle());
        }
        return titles;
    }
}
