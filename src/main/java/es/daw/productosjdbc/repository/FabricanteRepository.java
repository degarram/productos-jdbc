package es.daw.productosjdbc.repository;


import es.daw.productosjdbc.model.Fabricante;
import org.springframework.stereotype.Repository;

@Repository
public interface FabricanteRepository extends GenericRepository<Fabricante, Integer> {
}