package zw.mohcc.org.prep.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MigrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testExcelUpload() throws Exception {

        byte[] content = Files.readAllBytes(Paths.get("/home/devoop/Downloads/file.xlsx"));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                content
        );

        mockMvc.perform(multipart("/api/migrate/import-excel")
                        .file(file))
                .andExpect(status().isOk());
    }
}