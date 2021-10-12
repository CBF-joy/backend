package com.example.joy_ocean.controller;


import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.example.joy_ocean.exception.FileStorageException;
import com.example.joy_ocean.model.*;
import com.example.joy_ocean.payload.*;
import com.example.joy_ocean.payload.ApiResponse;
import com.example.joy_ocean.repository.*;


import com.example.joy_ocean.security.CurrentUser;
import com.example.joy_ocean.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.StorageOptions;


@RequestMapping("/api")
@RestController
public class ToonController {

    private static final Logger logger = LoggerFactory.getLogger(ToonController.class);

    @Autowired
    private ExhibitionRepository exhibitionRepository;

    @Autowired
    private PaintingRepository paintingRepository;

    @Autowired
    private PosterRepository posterRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RateRepository rateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private PostRepository postRepository;




    // 전시회 등록 - 관리자 권한
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/newAdd", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Exhibition newAdd(@RequestParam("title") String title, @RequestParam("description") String description,
                             @RequestParam("sponsor") String sponsor, @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
                             @RequestParam("file") MultipartFile file, @CurrentUser UserPrincipal currentUser) {

        UUID uid = UUID.randomUUID();
        String filename = uid.toString() + file.getContentType().replace('/','.').toString();
        String fileName = StringUtils.cleanPath(filename);
        try {
            String keyFileName = "oidc-project-317910-8a43df642d7f.json";
            InputStream keyFile = ResourceUtils.getURL("classpath:" + keyFileName).openStream();
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(keyFile))
                    .build().getService();
            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder("oidc-file-storage", "poster/"+fileName).build(), //get original file name
                    file.getBytes(), // the file
                    BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ) // Set file permission
            );
            String fileUri = "https://storage.googleapis.com/oidc-file-storage/poster/" + filename;
            Poster poster = new Poster(fileName, file.getContentType(), fileUri, file.getSize());
            User user = userRepository.findByUsername(currentUser.getUsername()).orElse(null);
            Exhibition exhibition = new Exhibition(sponsor, fromDate, toDate, title, description, user, poster);
            poster.setExhibition(exhibition);
            exhibitionRepository.save(exhibition);
            posterRepository.save(poster);
            return exhibition;
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file " +fileName + ". Please try again!", ex);
        }
    }

    // 관리자 마이페이지 -> 자신이 등록한 전시회 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getMyExhibition")
    public Collection<Exhibition> getMyExhibition(@CurrentUser UserPrincipal currentUser){
        User user = userRepository.findByUsername(currentUser.getUsername()).orElse(null);
        return exhibitionRepository.findbyUser(user.getId());
    }

    //기존 전시회 수정을 위해 한 전시회 가져오기
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getExhibitionDetail/{id}")
    public Optional<Exhibition> getExhibitionDetail(@PathVariable Long id){
        return exhibitionRepository.findById(id);
    }

    // 전시회 수정 업로드
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/editExhibition/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Optional<Exhibition>  uploadEditExhibition(@PathVariable Long id, @RequestParam("title") String title, @RequestParam("description") String description,
                                     @RequestParam("sponsor") String sponsor, @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
                                     @RequestParam("file") MultipartFile file) throws ParseException {


        Exhibition exhibition = exhibitionRepository.findById(id).get();
        exhibition.setTitle(title);
        exhibition.setDescription(description);
        exhibition.setSponsor(sponsor);
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        exhibition.setFromDate(transFormat.parse(fromDate));
        exhibition.setToDate(transFormat.parse(toDate));
        Poster poster1 = posterRepository.getPosterByExhibition(id).get();

        UUID uid = UUID.randomUUID();
        String filename = uid.toString() + file.getContentType().replace('/','.').toString();
        String fileName = StringUtils.cleanPath(filename);
        try {
            String keyFileName = "oidc-project-317910-8a43df642d7f.json";
            InputStream keyFile = ResourceUtils.getURL("classpath:" + keyFileName).openStream();
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(keyFile))
                    .build().getService();
            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder("oidc-file-storage", "poster/"+fileName).build(), //get original file name
                    file.getBytes(), // the file
                    BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ) // Set file permission
            );
            String fileUri = "https://storage.googleapis.com/oidc-file-storage/poster/" + filename;
            Poster poster = new Poster(fileName, file.getContentType(), fileUri, file.getSize());
            exhibition.setPoster(poster);
            poster.setExhibition(exhibition);
            exhibitionRepository.save(exhibition);
            posterRepository.deletePoster(poster1.getPosno());
            return exhibitionRepository.findById(id);
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file " +fileName + ". Please try again!", ex);
        }
    }

    // 수정한 전시회 업로드 (파일 바뀌지 않았을 때)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/editExhibitionExceptFile/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Optional<Exhibition>  uploadEditExhibitionExceptFile(@PathVariable Long id, @RequestParam("title") String title,
                                                     @RequestParam("description") String description, @RequestParam("sponsor") String sponsor,
                                                     @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate) throws ParseException {

        Exhibition exhibition = exhibitionRepository.findById(id).get();
        exhibition.setTitle(title);
        exhibition.setDescription(description);
        exhibition.setSponsor(sponsor);
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        exhibition.setFromDate(transFormat.parse(fromDate));
        exhibition.setToDate(transFormat.parse(toDate));
        exhibitionRepository.save(exhibition);
        return exhibitionRepository.findById(id);
    }


    // 전시회 상태변화 - 진행중(ing)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/setStatusIng/{id}")
    public Exhibition setStatusIng(@PathVariable Long id){
        Exhibition exhibition = exhibitionRepository.findById(id).get();
        exhibition.setStatus("ing");
        exhibitionRepository.save(exhibition);
        return exhibition;
    }

    // 전시회 상태변화 - 진행중(end)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/setStatusEnd/{id}")
    public Exhibition setStatusEnd(@PathVariable Long id){
        Exhibition exhibition = exhibitionRepository.findById(id).get();
        exhibition.setStatus("end");
        exhibitionRepository.save(exhibition);
        return exhibition;
    }


    // 전시회 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteExhibition/{id}")
    public void deleteExhibition(@PathVariable Long id) {
        exhibitionRepository.deleteById(id);
    }

   // 진행중인 전시회
   @GetMapping("/getIngExhibition")
   public Collection<Exhibition> getIngExhibition() {
       return exhibitionRepository.findbyStatus("ing");
   }


   /* @@ 전시회 세부 페이지 @@ */
    // 전시 상세 정보
    @GetMapping("/getIngExhibitionDetail/{id}")
    public Optional<Exhibition> getIngExhibitionDetail(@PathVariable Long id){
        return exhibitionRepository.findById(id);
    }

    // 댓글 등록
    @PostMapping("/saveComment/{id}")
    public Exhibition createComment(@PathVariable Long id, @RequestParam("username") String username,
                                    @RequestParam("comment") String comment) {
        Exhibition exhibition = exhibitionRepository.findById(id).get();
        Comment com = new Comment(username, comment);
        com.setExhibition(exhibition);
        exhibition.getComments().add(com);
        return exhibitionRepository.save(exhibition);
    }

    // 댓글 수정을 위해 가져오기
    @GetMapping("/getComment/{ex_id}")
    public Collection<Comment> getComment(@PathVariable Long ex_id) {
        return commentRepository.getComment(ex_id);
    }

    // 댓글 수정
    @PutMapping("/editComment/{com_id}")
    public Comment editComment(@PathVariable Long com_id, @RequestParam("comment") String comment){
        Comment com = commentRepository.findById(com_id).get();
        com.setComment(comment);
        return commentRepository.save(com);
    }

    // 댓글 삭제
    @DeleteMapping("/deleteComment/{com_id}")
    public void deleteCommen(@PathVariable Long com_id){
        commentRepository.deleteById(com_id);
    }


    // Rate 등록
    @PostMapping("/saveRate/{id}")
    public Exhibition saveRate(@PathVariable Long id, @RequestParam("username") String username, @RequestParam("rate") Integer rate){
        Exhibition exhibition = exhibitionRepository.findById(id).get();
        Rate r = new Rate(username, rate);
        r.setExhibition(exhibition);
        exhibition.getRate().add(r);
        return exhibitionRepository.save(exhibition);
    }


    // 기존 Rate 가져오기
    @GetMapping(value={"/fetchRate/{id}/{username}"})
    public Optional<Rate> fetchRate(@PathVariable("id") Long id, @PathVariable("username") String user){
        return rateRepository.getRateByEx_Id(id, user);
    }

    // Rate 수정
    @PutMapping("/editRate/{id}")
    public Rate uploadEditRate(@PathVariable Long id,@RequestParam("username") String user, @RequestParam("rate") Integer rate){
        Rate r = rateRepository.getRateByEx_Id(id, user).get();
        r.setRate(rate);
        return rateRepository.save(r);
    }

    // Rate 평균
    @GetMapping("/getAvgRate/{id}")
    public Double getAvgRate(@PathVariable Long id){
        return rateRepository.getAvgRate(id);
    }


    // 에정인 전시회
    @GetMapping("/getPreExhibition")
    public Collection<Exhibition> getPreExhibition() {
        return exhibitionRepository.findbyStatus("pre");
    }


    public ResponseEntity<?> SameToken_PaintingSaveErrorMessage() {
        return new ResponseEntity(new ApiResponse(false, "이미 동일한 토큰이 해당 전시회에 등록되었습니다."),
                HttpStatus.BAD_REQUEST);
    }

    // 작품등록
    @PostMapping("/savePainting/{id}")
    public Object savePainting(@PathVariable Long id, @RequestParam("username") String username,
                                    @RequestParam("title") String title, @RequestParam("description") String description,
                                    @RequestParam("file_uri") String file_uri) {
        if(! paintingRepository.findByExhibitionAndFileUri(id,file_uri).isEmpty()) return SameToken_PaintingSaveErrorMessage();
        Exhibition exhibition = exhibitionRepository.findById(id).get();
        Painting painting = new Painting(file_uri, title, description, username);
        painting.setExhibition(exhibition);
        exhibition.getPainting().add(painting);
        return exhibitionRepository.save(exhibition);
    }


    // 자신이 등록한 전시회 작품 조회
    @GetMapping("/getMyPainting")
    public ArrayList<MyPaintings> getMyPainting(@CurrentUser UserPrincipal currentUser) throws ParseException {

        List<Painting> paintingList = paintingRepository.getByUsername(currentUser.getUsername());
        ArrayList<MyPaintings> myPaintingsList = new ArrayList<>();
        Iterator<Painting> iter = paintingList.iterator();
        while(iter.hasNext()){
            MyPaintings myPainting = new MyPaintings(iter.next());
            myPaintingsList.add(myPainting);
        }
        return myPaintingsList;
    }

    public ResponseEntity<?> PaintingDeleteErrorMessage() {
        return new ResponseEntity(new ApiResponse(false, "이미 전시가 등록되었습니다."),
                HttpStatus.BAD_REQUEST);
    }

    // 전시회 작품 삭제
    @DeleteMapping("/deletePainting/{painting_id}")
    public Object deletePainting(@PathVariable Long painting_id){
        Painting painting = paintingRepository.findById(painting_id).get();
        if(!painting.getExhibition().getStatus().equals("pre")) return PaintingDeleteErrorMessage();
        paintingRepository.deleteById(painting_id);
        return null;
    }



    // 작품 토큰 발행
    @PreAuthorize("hasRole('AUTHOR')")
    @PostMapping(value = "/saveImagefile", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public FileUriResponse saveImagefile( @RequestParam("file") MultipartFile file, @CurrentUser UserPrincipal currentUser) {

        UUID uid = UUID.randomUUID();
        String filename = uid.toString() + file.getContentType().replace('/','.').toString();
        String fileName = StringUtils.cleanPath(filename);
        try {
            String keyFileName = "oidc-project-317910-8a43df642d7f.json";
            InputStream keyFile = ResourceUtils.getURL("classpath:" + keyFileName).openStream();
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(keyFile))
                    .build().getService();
            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder("oidc-file-storage", "image/"+fileName).build(), //get original file name
                    file.getBytes(), // the file
                    BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ) // Set file permission
            );
            String fileUri = "https://storage.googleapis.com/oidc-file-storage/image/" + filename;
            FileUriResponse response = new FileUriResponse();
            response.setFile_uri(fileUri);
            return response;
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file " +fileName + ". Please try again!", ex);
        }
    }



    // 작품설명
    @PreAuthorize("hasRole('AUTHOR')")
    @PostMapping(value = "/saveDescriptionfile")
    public FileUriResponse saveDescriptionfile( @RequestParam("desc") String str1) throws IOException {

       // MultipartFile file = new MockMultipartFile("1.png", new FileInputStream(new File("C:\\Users\\sujin\\OneDrive\\바탕 화면\\SUJIN\\2021\\졸프_OIDC대상\\캡처1.png")));
        String str = "{\"des\" : "+ str1 + " }";
        UUID uid = UUID.randomUUID();
        String filename = uid.toString() + ".json";
        String fileName = StringUtils.cleanPath(filename);
        try {
            String keyFileName = "oidc-project-317910-8a43df642d7f.json";
            InputStream keyFile = ResourceUtils.getURL("classpath:" + keyFileName).openStream();
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(keyFile))
                    .build().getService();
            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder("oidc-file-storage", "description/"+fileName).build(), //get original file name
                    //file.getBytes(), // the file
                    str.getBytes("euc-kr"),
                    BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ) // Set file permission
            );
            String fileUri = "https://storage.googleapis.com/oidc-file-storage/description/" + filename;
            FileUriResponse response = new FileUriResponse();
            response.setFile_uri(fileUri);
            return response;
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file " +fileName + ". Please try again!", ex);
        }
    }



    public ResponseEntity<?> SameToken_AuctionSaveErrorMessage() {
        return new ResponseEntity(new ApiResponse(false, "이미 동일한 토큰이 경매에 등록되었습니다."),
                HttpStatus.BAD_REQUEST);
    }

    // 경매 등록
    @PostMapping("/saveAuction")
    public Object saveAuction(@RequestParam("token_id") String token_id, @RequestParam("owner_address") String owner_address,
                              @RequestParam("minium_price") Double minium_price, @RequestParam("fromDate") String fromDate,
                              @RequestParam("toDate") String toDate, @CurrentUser UserPrincipal currentUser) throws ParseException {
        if(! auctionRepository.findByToken(token_id).isEmpty() ){return this.SameToken_AuctionSaveErrorMessage();}
        Auction auction = new Auction(token_id, fromDate, toDate, owner_address, currentUser.getUsername(), minium_price);
        return auctionRepository.save(auction);
    }


    // 자신의 경매 조회
    @GetMapping("/getMyAuction")
    public Collection<Auction> getMyAuction( @CurrentUser UserPrincipal currentUser){
        return auctionRepository.findbyUsername(currentUser.getUsername());
    }


    public ResponseEntity<?> AuctionDeleteErrorMessage() {
        return new ResponseEntity(new ApiResponse(false, "이미 낙찰을 끝냈습니다."),
                HttpStatus.BAD_REQUEST);
    }

    // 경매 삭제
    @DeleteMapping("/deleteAuction/{auction_id}")
    public Object deleteAuction(@PathVariable Long auction_id){
        if(auctionRepository.findbyId(auction_id).get().getStatus().equals("end")) return AuctionDeleteErrorMessage();
        auctionRepository.deleteById(auction_id);
        return null;
    }


    // 진행중인 경매
    @GetMapping("/getIngAuction")
    public Collection<Auction> getIngAuction() {
        return auctionRepository.findByStatus("ing");
    }


    // 경매 상세 페이지
    @GetMapping("/getAuctionDetail/{auction_id}")
    public Optional<Auction> getAuctionDetail(@PathVariable Long auction_id) {
        return auctionRepository.findbyId(auction_id);
    }


    public ResponseEntity<?> SameBidSaveErrorMessage() {
        return new ResponseEntity(new ApiResponse(false, "이미 경매에 참여하셨습니다."),
                HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> miniumpriceErrorMessage() {
        return new ResponseEntity(new ApiResponse(false, "경매 최저가 이상 값을 등록해주세요."),
                HttpStatus.BAD_REQUEST);
    }

    // 응찰 등록
    @PostMapping("/saveBid/{auction_id}")
    public Object saveBid(@PathVariable Long auction_id, @RequestParam("price") Double price,
                              @CurrentUser UserPrincipal currentUser) throws ParseException {
        if(! bidRepository.findSameBid(auction_id,currentUser.getAddress()).isEmpty()) return SameBidSaveErrorMessage();
        Auction auction = auctionRepository.findbyId(auction_id).get();
        if(price < auction.getMinium_price()) return miniumpriceErrorMessage();
        Bid bid = new Bid(auction.getToken_id(), currentUser.getAddress(), price, auction);
        return bidRepository.save(bid);
    }

    //응찰 수정
    @PutMapping("/editBid/{auction_id}")
    public Object editBid(@PathVariable Long auction_id, @RequestParam("price") Double price,
                          @CurrentUser UserPrincipal currentUser){
        Bid bid = bidRepository.findByUserandAuction(auction_id,currentUser.getAddress()).get();
        Auction auction = auctionRepository.findbyId(auction_id).get();
        if(price < auction.getMinium_price()) return miniumpriceErrorMessage();
        bid.setPrice(price);
        return bidRepository.save(bid);
    }


    // 응찰 평균가
    @GetMapping("/getAvgBid/{auction_id}")
    public Double getAvgBid(@PathVariable Long auction_id){
        return bidRepository.getAvgBidByAuction(auction_id);
    }

    // 응찰 개수
    @GetMapping("/getCountBid/{auction_id}")
    public Long getCountBid(@PathVariable Long auction_id){
        return bidRepository.getCountBidByAuction(auction_id);
    }

    // 현재 최고가 응찰
    @GetMapping("/getMaxBid/{auction_id}")
    public Optional<Bid>  getMaxBid(@PathVariable Long auction_id){
        return bidRepository.getMaxBidByAuction(auction_id);
    }


    // 낙찰
    @GetMapping("/saveSuccessfulBid/{auction_id}")
    public Auction saveSuccessfulBid(@PathVariable Long auction_id){
        Bid bid = bidRepository.getMaxBidByAuction(auction_id).get();
        Auction auction = auctionRepository.findbyId(auction_id).get();
        auction.setSuccessful_bid_price(bid.getPrice().toString());
        auction.setSuccessful_bid_useraddress(bid.getUser_address());
        auction.setStatus("end");
        bid.setIsSuccess("yes");
        bidRepository.save(bid);
        auctionRepository.save(auction);
        return auction;
    }



    // 자신이 등록한 응찰
    @GetMapping("/getMyBid")
    public ArrayList<MyBids> getMyBid(@CurrentUser UserPrincipal currentUser) throws ParseException {
        List<Bid> bidList = bidRepository.findByUser(currentUser.getAddress());
        ArrayList<MyBids> myBidsList = new ArrayList<>();
        Iterator<Bid> iter = bidList.iterator();
        while(iter.hasNext()){
            MyBids myBid = new MyBids(iter.next());
            myBidsList.add(myBid);
        }
        return myBidsList;
    }


    public ResponseEntity<?> BidDeleteErrorMessage() {
        return new ResponseEntity(new ApiResponse(false, "이미 끝난 경매입니다."),
                HttpStatus.BAD_REQUEST);
    }

    // 경매 삭제
    @DeleteMapping("/deleteBid/{bid_id}")
    public Object deleteBid(@PathVariable Long bid_id){
        if(bidRepository.findById(bid_id).get().getAuction().getStatus().equals("end")) return BidDeleteErrorMessage();
        bidRepository.deleteById(bid_id);
        return null;
    }



    // token_id -> 경매
    @GetMapping("/getAuctionByToken/{token_id}")
    public Optional<Auction> getAuctionByToken(@PathVariable String token_id) {
        return auctionRepository.findIngByToken(token_id);
    }


    // 경매 상태변화 - 진행중(ing)
    @GetMapping("/setAuctionStatusIng/{id}")
    public Auction setAuctionStatusIng(@PathVariable Long id){
        Auction auction = auctionRepository.findbyId(id).get();
        auction.setStatus("ing");
        auctionRepository.save(auction);
        return auction;
    }

    // 경매 상태변화 - 진행중(end)
    @GetMapping("/setAuctionStatusEnd/{id}")
    public Auction setAuctionStatusEnd(@PathVariable Long id){
        Auction auction = auctionRepository.findbyId(id).get();
        auction.setStatus("end");
        auctionRepository.save(auction);
        return auction;
    }

    //경매 - 응찰 내역 조회
    @GetMapping("/getMyBidByAuction/{auction_id}")
    public Optional<Bid> getMyBidByAuction( @PathVariable Long auction_id, @CurrentUser UserPrincipal currentUser) throws ParseException {
       return bidRepository.findByUserandAuction( currentUser.getAddress(),auction_id);
    }




    //기부 등록
    @PostMapping("/savePost/{painting_id}")
    public Post savePost(@PathVariable Long painting_id, @RequestParam("klay") Double klay, @RequestParam("address") String address ){
        Painting painting = paintingRepository.findById(painting_id).get();
        Post post = new Post(address, painting, klay);
        Exhibition exhibition = painting.getExhibition();
        exhibition.setKlay(exhibition.getKlay() + klay);
        exhibitionRepository.save(exhibition);
        return postRepository.save(post);
    }

    // 자신이 등록한 기부
    @GetMapping("/getMyPost")
    public Collection<Post> getMyPost(@CurrentUser UserPrincipal currentUser) {
        Collection<Post> posts = postRepository.findbyUser(currentUser.getAddress());
        return posts;
    }


    //전시회 기부 총 금액
    @GetMapping("/getCountPostByExhibit/{exhibit_id}")
    public CountPostResponse getCountPostByExhibit(@PathVariable Long exhibit_id, @CurrentUser UserPrincipal currentUser) {
        Double all_klay = postRepository.getSumKlayByExhibition(exhibit_id);
        Exhibition exhibition = exhibitionRepository.findById(exhibit_id).get();
        CountPostResponse countPostResponse = new CountPostResponse(all_klay,exhibition );
        return countPostResponse;
    }

    //기부금액 순 전시회 목록
    @GetMapping("/getExhibitionRankingBySumPostKlay")
    public Collection<Exhibition> getExhibitionRankingBySumPostKlay() {
        return exhibitionRepository.findRankingbyKlay();
    }










/*
    // 새 웹툰 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/newAdd", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Toon newAdd(@RequestParam("title") String title, @RequestParam("artist") String artist,
            @RequestParam("day") String day, @RequestParam("genre") String genre,
            @RequestParam("file") MultipartFile file) {

        
        
        Toon toon = new Toon(title, artist, day, genre);
        ToonThumbnail toonThumbnail = toonThumbnailService.saveThumbnail(file);
        
        toon.setToonThumbnail(toonThumbnail);

        toonThumbnail.setToon(toon);

        Toon result = toonRepository.save(toon);

        return result;

    }

    // 새 에피소드 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/newEpi", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Episode newEpi(@RequestParam("epiTitle") String epiTitle, @RequestParam("toonId") Toon toon, @RequestParam("webtoonId") Integer webtoonId,
            @RequestParam("eFile") MultipartFile eFile, @RequestParam("mFile") MultipartFile mFile) {

        
        
        Episode episode = new Episode(epiTitle, webtoonId, toon);
        EpiThumbnail epiThumbnail = epiThumbnailService.saveEpiThumbnail(eFile);
        EpiToon epiToon = epiToonService.saveEpiToon(mFile);


        episode.setEpiToon(epiToon);
        epiToon.setEpisode(episode);

        episode.setEpiThumbnail(epiThumbnail);
        epiThumbnail.setEpisode(episode);

        Episode result = episodeRepository.save(episode);
        
        return result;

    }




    // 새 에피소드 등록을 위한 webtoonId 값 가져오기
    @GetMapping("/getToonIdAndName")
    public List<Map<String, Object>> getTIAN() {
        return toonRepository.getToonIdAndName();
    }


    @GetMapping("/getEpi/{id}")
    public Collection<Episode> getEpi(@PathVariable int id) {
        return episodeRepository.getEpi(id);
    }

    
    @GetMapping("/getEpiById/{id}")
    public Optional<Episode> getEpiById(@PathVariable int id) {
        return episodeRepository.findById(id);
    }

    @GetMapping("/getToonById/{id}")
    public Optional<Toon> getToonById(@PathVariable int id) {
        return toonRepository.findById(id);
    }


    @GetMapping("/getToonThumbnailById/{id}")
    public Optional<ToonThumbnail> getToonThumbnailById(@PathVariable int id) {
        return toonThumbnailRepository.getToonThumbnailByID(id);
    }

    @DeleteMapping("/deleteToonThumbnail/{id}")
    public void deleteToonThumbnail(@PathVariable Integer id) {
        toonThumbnailRepository.deleteToonThumbnail(id);
    }



    //기존 에피소드 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteEpi/{id}")
    public void deleteEpi(@PathVariable Integer id) {
        episodeRepository.deleteById(id);
    }

    //기존 에피소드 수정을 위해 한 에피소드 가져오기
    @GetMapping("/getEditEpi/{id}")
    public Optional<Episode> getEditEpiById(@PathVariable int id){
        return episodeRepository.findById(id);

    }

    //에피소드 수정 용 웹툰 타이틀 가져오기
    @GetMapping("/getToonTitle/{id}")
    public Optional<Toon> getToonTitle(@PathVariable int id){
        return toonRepository.findById(id);
    }

    


    // 수정한 웹툰 업로드
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/uploadEditToon/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Toon uploadEditToon(@PathVariable int id, @RequestParam("title") String title, @RequestParam("artist") String artist,
            @RequestParam("day") String day, @RequestParam("genre") String genre,
            @RequestParam("file") MultipartFile file) {
        

        Toon toon = toonRepository.findById(id).get();
        toon.setTitle(title);
        toon.setArtist(artist);
        toon.setDay(day);
        toon.setGenre(genre);

        ToonThumbnail toonThumbnail = toonThumbnailService.saveThumbnail(file);
        
        toon.setToonThumbnail(toonThumbnail);

        toonThumbnail.setToon(toon);

        Toon result = toonRepository.save(toon);

        return result;

    }

    // 수정한 웹툰 업로드 (파일 바뀌지 않았을 때)
    @PutMapping(value = "/uploadEditToonExceptFile/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Toon uploadEditToonExceptFile(@PathVariable int id, @RequestParam("title") String title, @RequestParam("artist") String artist,
            @RequestParam("day") String day, @RequestParam("genre") String genre) {
        

        Toon toon = toonRepository.findById(id).get();
        toon.setTitle(title);
        toon.setArtist(artist);
        toon.setDay(day);
        toon.setGenre(genre);

        Toon result = toonRepository.save(toon);

        return result;

    }

    // 수정한 에피소드 업로드
    @PutMapping(value = "/uploadEditEpi/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Episode uploadEditEpi(@PathVariable int id, @RequestParam("epiTitle") String epiTitle,
            @RequestParam("eFile") MultipartFile eFile, @RequestParam("mFile") MultipartFile mFile) {
        

        Episode episode = episodeRepository.findById(id).get();
        episode.setEpiTitle(epiTitle);

        EpiThumbnail epiThumbnail = epiThumbnailService.saveEpiThumbnail(eFile);
        EpiToon epiToon = epiToonService.saveEpiToon(mFile);

        
        
        episode.setEpiToon(epiToon);
        epiToon.setEpisode(episode);

        episode.setEpiThumbnail(epiThumbnail);
        epiThumbnail.setEpisode(episode);

        Episode result = episodeRepository.save(episode);

        return result;

    }

    // 수정한 에피소드 업로드 (문서만 변경됐을 경우)
    @PutMapping(value = "/uploadEditEpiExceptTaM/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Episode uploadEditEpiExceptTaM(@PathVariable int id, @RequestParam("epiTitle") String epiTitle) {
        

        Episode episode = episodeRepository.findById(id).get();
        episode.setEpiTitle(epiTitle);

        Episode result = episodeRepository.save(episode);

        return result;

    }

    // 수정한 에피소드 업로드(썸네일만 변경됐을 경우)
    @PutMapping(value = "/uploadEditEpiExceptM/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Episode uploadEditEpiExceptM(@PathVariable int id, @RequestParam("epiTitle") String epiTitle,
            @RequestParam("eFile") MultipartFile eFile) {
        

        Episode episode = episodeRepository.findById(id).get();
        episode.setEpiTitle(epiTitle);

        EpiThumbnail epiThumbnail = epiThumbnailService.saveEpiThumbnail(eFile);
        

        episode.setEpiThumbnail(epiThumbnail);
        epiThumbnail.setEpisode(episode);

        Episode result = episodeRepository.save(episode);

        return result;

    }

    // 수정한 에피소드 업로드(본문만 변경됐을 경우)
    @PutMapping(value = "/uploadEditEpiExceptT/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Episode uploadEditEpiExceptT(@PathVariable int id, @RequestParam("epiTitle") String epiTitle,
            @RequestParam("mFile") MultipartFile mFile) {
        

        Episode episode = episodeRepository.findById(id).get();
        episode.setEpiTitle(epiTitle);

        EpiToon epiToon = epiToonService.saveEpiToon(mFile);

        
        
        episode.setEpiToon(epiToon);
        epiToon.setEpisode(episode);


        Episode result = episodeRepository.save(episode);

        return result;

    }

    @GetMapping("/getEpiThumbnailById/{id}")
    public Optional<EpiThumbnail> getEpiThumbnailById(@PathVariable int id) {
        return epiThumbnailRepository.getEpiThumbnailById(id);
    }

    @DeleteMapping("/deleteEpiThumbnail/{id}")
    public void deleteEpiThumbnail(@PathVariable Integer id) {
        epiThumbnailRepository.deleteEpiThumbnail(id);
    }

    @DeleteMapping("/deleteEpiToon/{id}")
    public void deleteEpiToon(@PathVariable Integer id) {
        epiToonRepository.deleteEpiToon(id);
    }

    @GetMapping("/getEpiToon/{id}")
    public Optional<EpiToon> getEpiToon(@PathVariable int id) {
        return epiToonRepository.getEpiToon(id);
    }


*/
    
}
