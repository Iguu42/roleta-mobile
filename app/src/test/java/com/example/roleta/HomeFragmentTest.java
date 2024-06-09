package com.example.roleta;

import static org.mockito.Mockito.verify;

import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



public class HomeFragmentTest {

    @Mock
    Button mockBtPredRoulettes;

    @Mock
    Button mockBtCreateRoulettes;

    @InjectMocks
    HomeFragment fragment;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);

        fragment.btPredRoulettes = mockBtPredRoulettes;
        fragment.btCreateRoulettes = mockBtCreateRoulettes;

    }
    @Test
    public void testPredRoulettesButtonClicked() {
        fragment.btPredRoulettes.performClick();
        verify(mockBtPredRoulettes).performClick();
    }

    @Test
    public void testCreateRoulettesButtonClicked() {
        fragment.btCreateRoulettes.performClick();
        verify(mockBtCreateRoulettes).performClick();
    }
}
