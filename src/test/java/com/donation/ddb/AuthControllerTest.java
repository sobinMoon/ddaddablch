package com.donation.ddb;

import com.donation.ddb.Controller.AuthController;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.WalletNonceRequestDTO;
import com.donation.ddb.Service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;
    private StudentUser testStudentUser;
    private String testNonce;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        mockMvc= MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper=new ObjectMapper();

        //테스트용 StudentUser 객체 생성
        testStudentUser=new StudentUser();
        testStudentUser.setSEmail("test@sookmyung.ac.kr");

        //테스트용 논스 생성
        testNonce= UUID.randomUUID().toString();
    }
/*
    @Test
    void nonce발급받기() throws Exception{

        //Given
        String email="test@sookmyung.ac.kr";
        String walletAddress="0x12345";

        WalletNonceRequestDTO requestDto = new WalletNonceRequestDTO();
        requestDto.setEmail(email);
        requestDto.setWalletAddress(walletAddress);



        //When



        //Then



    }

    @Test
    void nonce발급예외_이메일존재안함() throws Exception{

        //Given


        //When



        //Then




    }
    @Test
    void nonce발급예외_필수항목없음() throws Exception{
        //Given


        //When



        //Then

    }
*/

}
