package junhyeok.giveme.readme.dto.response;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ReadRepositoriesRes {
    private List<RepositoryInfo> repositories;

    public ReadRepositoriesRes(RepositoryInfo[] arr){
        this.repositories = new ArrayList<>(Arrays.stream(arr).toList());
    }
}
