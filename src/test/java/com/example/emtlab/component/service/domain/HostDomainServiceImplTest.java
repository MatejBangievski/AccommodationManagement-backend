package com.example.emtlab.component.service.domain;

import com.example.emtlab.events.HostChangedEvent;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.enumerations.EntityChangeType;
import com.example.emtlab.model.projections.HostProjection;
import com.example.emtlab.model.views.HostsPerCountryView;
import com.example.emtlab.repository.HostRepository;
import com.example.emtlab.repository.HostsPerCountryViewRepostiory;
import com.example.emtlab.service.domain.CountryService;
import com.example.emtlab.service.domain.impl.HostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HostDomainServiceImplTest {

    @Mock
    private HostRepository hostRepository;

    @Mock
    private HostsPerCountryViewRepostiory hostsPerCountryViewRepostiory;

    @Mock
    private CountryService countryService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private HostServiceImpl hostService;

    private Host host;
    private Country country;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        country = new Country("Macedonia", "Europe");
        country.setId(1L);

        host = new Host("John", "Doe", country);
        host.setId(1L);
    }

    // ===========================
    // Basic CRUD tests
    // ===========================

    @Test
    void findAllShouldReturnAllHosts() {
        when(hostRepository.findAll()).thenReturn(Arrays.asList(host));

        List<Host> result = hostService.findAll();

        assertThat(result).containsExactly(host);
        verify(hostRepository, times(1)).findAll();
    }

    @Test
    void findByIdShouldReturnHostIfExists() {
        when(hostRepository.findById(1L)).thenReturn(Optional.of(host));

        Optional<Host> result = hostService.findById(1L);

        assertThat(result).isPresent().contains(host);
        verify(hostRepository, times(1)).findById(1L);
    }

    @Test
    void findByIdShouldReturnEmptyIfNotExists() {
        when(hostRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Host> result = hostService.findById(2L);

        assertThat(result).isEmpty();
        verify(hostRepository, times(1)).findById(2L);
    }

    @Test
    void saveShouldReturnSavedHostWhenCountryExists() {
        when(countryService.findById(1L)).thenReturn(Optional.of(country));
        when(hostRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Host> result = hostService.save(host);

        assertThat(result).isPresent();
        assertThat(result.get().getCountry()).isEqualTo(country);
        verify(hostRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1))
                .publishEvent(any(HostChangedEvent.class));
    }

    @Test
    void saveShouldReturnEmptyIfCountryDoesNotExist() {
        when(countryService.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Host> result = hostService.save(host);

        assertThat(result).isEmpty();
        verify(hostRepository, never()).save(any());
        verify(applicationEventPublisher, times(1))
                .publishEvent(any(HostChangedEvent.class));
    }

    @Test
    void updateShouldModifyExistingHost() {
        Host updatedHost = new Host("Jane", "Smith", country);
        when(hostRepository.findById(1L)).thenReturn(Optional.of(host));
        when(countryService.findById(1L)).thenReturn(Optional.of(country));
        when(hostRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Host> result = hostService.update(1L, updatedHost);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Jane");
        assertThat(result.get().getSurname()).isEqualTo("Smith");
        verify(hostRepository, times(1)).save(host);
        verify(applicationEventPublisher, times(1))
                .publishEvent(any(HostChangedEvent.class));
    }

    @Test
    void updateShouldModifyOnlyNonNullFields() {
        Host partialUpdate = new Host(null, "Smith", null);
        when(hostRepository.findById(1L)).thenReturn(Optional.of(host));
        when(hostRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Host> result = hostService.update(1L, partialUpdate);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("John");
        assertThat(result.get().getSurname()).isEqualTo("Smith");
        verify(hostRepository, times(1)).save(host);
        verify(applicationEventPublisher, times(1))
                .publishEvent(any(HostChangedEvent.class));
    }

    @Test
    void updateShouldReturnEmptyIfHostDoesNotExist() {
        when(hostRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Host> result = hostService.update(2L, host);

        assertThat(result).isEmpty();
        verify(hostRepository, never()).save(any());
        verify(applicationEventPublisher, never())
                .publishEvent(any(HostChangedEvent.class));
    }

    @Test
    void deleteByIdShouldDeleteIfExistsAndPublishEvent() {
        when(hostRepository.findById(1L)).thenReturn(Optional.of(host));
        doNothing().when(hostRepository).deleteById(1L);

        hostService.deleteById(1L);

        verify(hostRepository, times(1)).deleteById(1L);
        verify(applicationEventPublisher, times(1))
                .publishEvent(any(HostChangedEvent.class));
    }

    @Test
    void deleteByIdShouldDoNothingIfHostDoesNotExist() {
        when(hostRepository.findById(2L)).thenReturn(Optional.empty());

        hostService.deleteById(2L);

        verify(hostRepository, never()).deleteById(anyLong());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    void refreshMaterializedViewShouldCallRepository() {
        doNothing().when(hostsPerCountryViewRepostiory).refreshMaterializedView();

        hostService.refreshMaterializedView();

        verify(hostsPerCountryViewRepostiory, times(1)).refreshMaterializedView();
    }

    @Test
    void getHostsPerCountryShouldReturnListFromRepository() {
        HostsPerCountryView view = mock(HostsPerCountryView.class);
        when(hostsPerCountryViewRepostiory.findAll()).thenReturn(List.of(view));

        List<HostsPerCountryView> result = hostService.getHostsPerCountry();

        assertThat(result).containsExactly(view);
        verify(hostsPerCountryViewRepostiory, times(1)).findAll();
    }

    @Test
    void getNameAndSurnameShouldReturnListOfProjections() {
        HostProjection projection = mock(HostProjection.class);
        when(hostRepository.takeNameAndSurnameByProjection()).thenReturn(List.of(projection));

        List<HostProjection> result = hostService.getNameAndSurname();

        assertThat(result).containsExactly(projection);
        verify(hostRepository, times(1)).takeNameAndSurnameByProjection();
    }
}
