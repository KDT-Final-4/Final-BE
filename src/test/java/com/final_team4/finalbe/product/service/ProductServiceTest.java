package com.final_team4.finalbe.product.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.product.domain.Product;
import com.final_team4.finalbe.product.dto.ProductCreateRequestDto;
import com.final_team4.finalbe.product.dto.ProductCreateResponseDto;
import com.final_team4.finalbe.product.dto.ProductDetailResponseDto;
import com.final_team4.finalbe.product.dto.ProductListResponseDto;
import com.final_team4.finalbe.product.mapper.ProductMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  ProductMapper productMapper;

  @Mock
  ProductCategoryService productCategoryService;

  @InjectMocks
  ProductService productService;

  @DisplayName("카테고리 id를 찾아 insert 후 생성 응답을 반환한다")
  @Test
  void create_success() {
    // given
    ProductCreateRequestDto request = createRequest("book", "Clean Code", "https://example.com/book", "thumb.png", 25000L);
    given(productCategoryService.findIdByName("book")).willReturn(3L);
    willSetIdOnInsert(11L);

    // when
    ProductCreateResponseDto response = productService.create(request);

    // then
    assertThat(response.getId()).isEqualTo(11L);
    assertThat(response.getCategory()).isEqualTo(3L);
    assertThat(response.getName()).isEqualTo("Clean Code");
    assertThat(response.getThumbnail()).isEqualTo("thumb.png");
    assertThat(response.getCreatedAt()).isNotNull();

    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productMapper).insert(captor.capture());
    assertThat(captor.getValue().getCategoryId()).isEqualTo(3L);
  }

  @DisplayName("카테고리 조회가 실패하면 생성 시 예외를 그대로 전달한다")
  @Test
  void create_categoryLookupFails() {
    ProductCreateRequestDto request = createRequest("unknown", "Item", "https://example.com/item", "thumb.png", 1000L);
    given(productCategoryService.findIdByName("unknown")).willThrow(new ContentNotFoundException("no category"));

    assertThatThrownBy(() -> productService.create(request))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("category");
  }

  @DisplayName("id로 상품을 찾으면 상세 DTO를 반환한다")
  @Test
  void findById_success() {
    LocalDateTime createdAt = LocalDateTime.of(2024, 5, 1, 10, 30);
    Product product = product(7L, 2L, "Keyboard", "https://example.com/keyboards/7", "thumb-7.png", 99000L, createdAt);
    given(productMapper.findById(7L)).willReturn(Optional.of(product));

    ProductDetailResponseDto response = productService.findById(7L);

    assertThat(response.getId()).isEqualTo(7L);
    assertThat(response.getCategory()).isEqualTo(2L);
    assertThat(response.getName()).isEqualTo("Keyboard");
    assertThat(response.getThumbnail()).isEqualTo("thumb-7.png");
    assertThat(response.getCreatedAt()).isEqualTo(createdAt);
  }

  @DisplayName("id로 상품을 찾을 수 없으면 예외를 던진다")
  @Test
  void findById_notFound() {
    given(productMapper.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> productService.findById(99L))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("제품");
  }

  @DisplayName("전체 상품 조회 시 목록 DTO로 매핑한다")
  @Test
  void findAll() {
    List<Product> products = List.of(
        product(1L, 4L, "Product-1", "https://example.com/products/1", "thumb-1.png", 1000L, LocalDateTime.now()),
        product(2L, 5L, "Product-2", "https://example.com/products/2", "thumb-2.png", 2000L, LocalDateTime.now())
    );
    given(productMapper.findAll()).willReturn(products);

    List<ProductListResponseDto> response = productService.findAll();

    assertThat(response)
        .hasSize(2)
        .extracting(ProductListResponseDto::getId, ProductListResponseDto::getName, ProductListResponseDto::getCategory)
        .containsExactly(
            tuple(1L, "Product-1", 4L),
            tuple(2L, "Product-2", 5L)
        );
  }

  @DisplayName("카테고리 id로 조회하면 해당 카테고리 상품만 반환한다")
  @Test
  void findByCategoryId() {
    List<Product> products = List.of(
        product(3L, 6L, "Coffee", "https://example.com/products/3", "thumb-3.png", 3000L, LocalDateTime.now()),
        product(4L, 6L, "Tea", "https://example.com/products/4", "thumb-4.png", 4000L, LocalDateTime.now())
    );
    given(productMapper.findByCategoryId(6L)).willReturn(products);

    List<ProductListResponseDto> response = productService.findByCategoryId(6L);

    assertThat(response)
        .hasSize(2)
        .allSatisfy(item -> assertThat(item.getCategory()).isEqualTo(6L));
  }

  private ProductCreateRequestDto createRequest(String category, String name, String link, String thumbnail, Long price) {
    return ProductCreateRequestDto.builder()
        .category(category)
        .name(name)
        .link(link)
        .thumbnail(thumbnail)
        .price(price)
        .build();
  }

  private Product product(Long id, Long categoryId, String name, String link, String thumbnail, Long price, LocalDateTime createdAt) {
    return Product.builder()
        .id(id)
        .categoryId(categoryId)
        .name(name)
        .link(link)
        .thumbnail(thumbnail)
        .price(price)
        .createdAt(createdAt)
        .build();
  }

  private void willSetIdOnInsert(Long id) {
    org.mockito.Mockito.doAnswer(invocation -> {
      Product argument = invocation.getArgument(0);
      ReflectionTestUtils.setField(argument, "id", id);
      return null;
    }).when(productMapper).insert(any(Product.class));
  }
}
