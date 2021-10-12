package com.example.joy_ocean.payload;

import com.example.joy_ocean.model.Painting;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;

@Getter
@Setter
public class MyPaintings {

    private Long pno;

    private String title;

    private String description;

    private String file_uri;

    private String username;

    private ExhibitionForPainting exhibition;

    public MyPaintings(Painting painting) throws ParseException {
        this.pno = painting.getPno();
        this.file_uri = painting.getFile_uri();
        this.title = painting.getTitle();
        this.description = painting.getDescription();
        this.username = painting.getUsername();
        ExhibitionForPainting ex = new ExhibitionForPainting(painting.getExhibition());
        this.exhibition = ex;
    }

    public MyPaintings() {

    }



}
