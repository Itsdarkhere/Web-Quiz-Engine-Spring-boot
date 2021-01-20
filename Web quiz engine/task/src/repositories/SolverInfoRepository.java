package repositories;

import dto.SolverInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SolverInfoRepository extends PagingAndSortingRepository<SolverInfo, Long> {

    Slice<SolverInfo> findAllByEmail(String email, Pageable pageable);

}