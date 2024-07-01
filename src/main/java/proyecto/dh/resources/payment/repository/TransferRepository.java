package proyecto.dh.resources.payment.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.dh.resources.payment.entity.Transfer;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
