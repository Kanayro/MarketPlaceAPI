package org.example.marketplaceservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplaceservice.dto.ProductDTO;
import org.example.marketplaceservice.mappers.ProductMapper;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Product;
import org.example.marketplaceservice.services.ProductService;
import org.example.marketplaceservice.util.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @Mock
    private ProductService productService;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductValidator validator;

    @InjectMocks
    private ProductController controller;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();
    }

    @Test
    public void shouldSaveProductDoExist() throws Exception {
        ProductDTO productDTO = new ProductDTO("name",100,10,true);
        String requestBody = mapper.writeValueAsString(productDTO);
        Product product = new Product();

        when(productMapper.convertToProduct(any(ProductDTO.class))).thenReturn(product);
        doNothing().when(validator).validate(any(Product.class),any(BindingResult.class));

        mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk());

        verify(productService,times(1)).save(product);
    }

    @Test
    public void shouldGetProduct() throws Exception {
        Product product = new Product();
        int id = 1;
        product.setId(id);
        ProductDTO productDTO = new ProductDTO("name",100,10,true);
        when(productService.findById(id)).thenReturn(product);
        when(productMapper.convertToProductDTO(product)).thenReturn(productDTO);
        mockMvc.perform(get("/product/{id}",id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(productDTO.getName()))
                .andExpect(jsonPath("$.price").value(productDTO.getPrice()))
                .andExpect(jsonPath("$.count").value(productDTO.getCount()))
                .andExpect(jsonPath("$.isCount").value(productDTO.getIsCount()));

    }

    @Test
    public void shouldGetProducts() throws Exception {
        Product product1 = new Product();
        Product product2 = new Product();
        String name1 = "name1";
        String name2 = "name2";
        product1.setName(name1);
        product2.setName(name2);

        List<Product> products = Arrays.asList(product1,product2);
        ProductDTO productDTO1 = new ProductDTO();
        ProductDTO productDTO2 = new ProductDTO();
        productDTO1.setName(name1);
        productDTO2.setName(name2);
        when(productService.getProducts()).thenReturn(products);
        when(productMapper.convertToProductDTO(any())).thenReturn(productDTO1).thenReturn(productDTO2);
        mockMvc.perform(get("/product/getProducts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[1].name").value(product2.getName()));
    }

    @Test
    public void shouldAddProductToCartDoExist() throws Exception {
        Product product = new Product();
        int id = 1;
        int count = 2;
        product.setId(id);
        product.setCount(true);
        product.setCount(10);
        when(productService.findById(id)).thenReturn(product);
        Cart cart = Mockito.mock(Cart.class);
        mockMvc.perform(get("/product/{id}/add",id)
                        .sessionAttr("user", cart)
                        .param("count", String.valueOf(count)))
                .andExpect(status().isOk());
        verify(cart,times(1)).addProduct(product, count);
    }

}
