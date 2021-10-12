package com.example.joy_ocean.model;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.joy_ocean.model.audit.DateAudit;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.NaturalId;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
            "username"
        }),
        @UniqueConstraint(columnNames = {
            "email"
        })
})
public class User extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 40)
    private String name;

    @NotBlank
    @Size(max = 15)
    private String username;

    @NaturalId
    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank
    @Size(max = 100)
    private String password;

    @Size(max = 100)
    private String address;

    @Size(max = 1024)
    private String public_key;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>(); //순서 상관 없는 집합


    @JsonManagedReference
    @OneToMany(fetch=FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "user")
    private Set<Exhibition> exhibitions = new HashSet<>();




    public User() {

    }

    public User(String name, String username, String email, String password) throws MalformedURLException {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        try {
            // 여기에 kas -> this.address, this.public_key 설정
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://wallet-api.klaytnapi.com/v2/account"))
                    .header("Content-Type", "application/json")
                    .header("x-chain-id", "1001")
                    .header("Authorization", "Basic S0FTS0JBRTY4RVg2Q1UxVFBHR0ZFRk5ROjZyUVhvdHFyRDREeVVucDJBLXlGeWRWNnBzVm1jUjhZOHU3N0xHZFM=")
                    .method("POST", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            JSONParser parser = new JSONParser();
            JSONObject jsonObj = new JSONObject();
            String jsonStr = response.body();
            try {
                jsonObj = (JSONObject) parser.parse(jsonStr);
                String address = (String) jsonObj.get("address");
                String public_key = (String) jsonObj.get("publicKey");
                this.address = address;
                this.public_key = public_key;
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}