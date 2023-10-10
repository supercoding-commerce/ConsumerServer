package com.github.messageconsumer.service.order;

import com.github.messageconsumer.dto.OrderRmqDto;
import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.entity.Seller;
import com.github.messageconsumer.entity.User;
import com.github.messageconsumer.repository.CartRepository;
import com.github.messageconsumer.repository.OrderRepository;
import com.github.messageconsumer.service.order.exception.OrderErrorCode;
import com.github.messageconsumer.service.order.exception.OrderException;
import com.github.messageconsumer.service.order.util.ValidateOrderMethod;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@TestPropertySource(locations = "classpath:application-test.yml")
@ExtendWith(MockitoExtension.class) // @Mock 사용을 위해 설정
class OrderConsumerServiceTest {
    @InjectMocks
    private OrderConsumerService orderConsumerService;

    @Mock
    private ValidateOrderMethod validateOrderMethod;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private Message message;

    @Mock
    private Channel channel;
    @Test
    void postOrder() throws IOException {
        OrderRmqDto orderRmqDto = new OrderRmqDto();
        orderRmqDto.setProductId(1L);
        orderRmqDto.setUserId(1L);
        orderRmqDto.setQuantity(100);
        Product mockProduct = new Product();
        mockProduct.setSeller(new Seller());
        User mockUser = new User();
        mockUser.setId(1L);

        // Mocking the MessageProperties
        MessageProperties mockMessageProperties = mock(MessageProperties.class);
        when(message.getMessageProperties()).thenReturn(mockMessageProperties);

        // Mocking the getDeliveryTag method
        when(mockMessageProperties.getDeliveryTag()).thenReturn(1L);

        when(validateOrderMethod.validateProduct(1L)).thenReturn(mockProduct);
        when(validateOrderMethod.validateUser(1L)).thenReturn(mockUser);
        when(validateOrderMethod.validateStock(anyInt(), eq(mockProduct))).thenReturn(true);

        orderConsumerService.postOrder(orderRmqDto, message, channel);

        verify(orderRepository, times(1)).save(any());


    }

    @Test
    void whenStockIsInvalid_ShouldHandleWithBasicNack() throws IOException {
        // Given
        OrderRmqDto orderRmqDto = createOrderRmqDto();
        setupMocksForOrder(orderRmqDto);
        when(validateOrderMethod.validateStock(anyInt(), any())).thenReturn(false); // Making stock invalid

        // When
        orderConsumerService.postOrder(orderRmqDto, message, channel);

        // Then
        verify(channel).basicNack(1L, false, false);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void whenCartIdIsNotNull_ShouldSaveOrderWithCart() throws IOException {
        // Given
        OrderRmqDto orderRmqDto = createOrderRmqDto();
        orderRmqDto.setCartId(123L); // setting a cart ID

        Product mockProduct = new Product();
        mockProduct.setSeller(new Seller());
        User mockUser = new User();
        mockUser.setId(1L);

        MessageProperties mockMessageProperties = mock(MessageProperties.class);
        when(message.getMessageProperties()).thenReturn(mockMessageProperties);
        when(mockMessageProperties.getDeliveryTag()).thenReturn(1L);
        when(validateOrderMethod.validateCart(anyLong(), anyLong())).thenReturn(new Cart());

        // Mocking the getDeliveryTag method
        when(mockMessageProperties.getDeliveryTag()).thenReturn(1L);

        when(validateOrderMethod.validateProduct(1L)).thenReturn(mockProduct);
        when(validateOrderMethod.validateUser(1L)).thenReturn(mockUser);
        when(validateOrderMethod.validateStock(anyInt(), eq(mockProduct))).thenReturn(true);

        // When
        orderConsumerService.postOrder(orderRmqDto, message, channel);

        // Then
        verify(cartRepository).save(any(Cart.class));
        verify(orderRepository).save(any());
    }

    @Test
    void whenIOExceptionOccurs_ShouldHandleWithBasicNack() throws IOException {
        // Given
        OrderRmqDto orderRmqDto = createOrderRmqDto();
        setupMocksForOrder(orderRmqDto);
        when(validateOrderMethod.validateProduct(anyLong())).thenThrow(new RuntimeException("Test Exception"));
        // When
        orderConsumerService.postOrder(orderRmqDto, message, channel);

        // Then
        verify(channel).basicNack(1L, false, false);
        verify(orderRepository, never()).save(any());
    }

    private OrderRmqDto createOrderRmqDto() {
        OrderRmqDto orderRmqDto = new OrderRmqDto();
        orderRmqDto.setProductId(1L);
        orderRmqDto.setUserId(1L);
        orderRmqDto.setQuantity(1);
        return orderRmqDto;
    }

    private void setupMocksForOrder(OrderRmqDto orderRmqDto) throws IOException {
        Product mockProduct = new Product();
        mockProduct.setSeller(new Seller());
        User mockUser = new User();
        mockUser.setId(1L);

        MessageProperties mockMessageProperties = mock(MessageProperties.class);
        when(message.getMessageProperties()).thenReturn(mockMessageProperties);
        when(mockMessageProperties.getDeliveryTag()).thenReturn(1L);
       //when(validateOrderMethod.validateProduct(orderRmqDto.getProductId())).thenReturn(mockProduct);
       // when(validateOrderMethod.validateUser(orderRmqDto.getUserId())).thenReturn(mockUser);
    }


    @Test
    void putOrderQueue() {
    }

    @Test
    void getKoreanTime() {
    }
}