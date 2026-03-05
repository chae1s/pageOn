package com.pageon.backend;

import com.pageon.backend.common.enums.RoleType;
import com.pageon.backend.security.JwtProvider;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TokenGenerator {

    public static void main(String[] args) throws IOException {


        String fileName = "tokens.csv";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write("userId,token\n");
        List<RoleType> roleTypes = new ArrayList<>();
        roleTypes.add(RoleType.ROLE_USER);

        String refresh = "GzfU/b9qs9W2gP67nl6aHNGZI09xR0J9ozVJk8snkqY";
        String access = "YdoOyhjCL6GGB3kA8s+cUoURVqiRVvErCZtUv4EE7Pg";

        JwtProvider jwtProvider = new JwtProvider(refresh, access);

        for (int i = 1; i <= 100000; i++) {
            String userId = "pageon" + i + "@mail.com";
            String accessToken = jwtProvider.generateAccessToken(userId, roleTypes);
            writer.write(userId + "," + accessToken + "\n");

            if (i % 10000 == 0) {
                System.out.println(i + "개 생성 완료...");
            }
        }

        System.out.println("생성 완료! 파일명: " + fileName);
        writer.close();
    }

}
