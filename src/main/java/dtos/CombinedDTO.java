package dtos;

public class CombinedDTO {
    private BoredomDTO boredomDTO;
    private CatDTO catDTO;
    private DogDTO dogDTO;
    private IpDTO ipDTO;

    public CombinedDTO(BoredomDTO boredomDTO, CatDTO catDTO, DogDTO dogDTO, IpDTO ipDTO) {
        this.boredomDTO = boredomDTO;
        this.catDTO = catDTO;
        this.dogDTO = dogDTO;
        this.ipDTO = ipDTO;
    }
}
