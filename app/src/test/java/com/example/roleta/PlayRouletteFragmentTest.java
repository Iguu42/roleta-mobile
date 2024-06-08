package com.example.roleta;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.roleta.PlayRouletteFragment;

@RunWith(MockitoJUnitRunner.class)
public class PlayRouletteFragmentTest {

    @Mock
    TextView mockTitleTextView;

    @InjectMocks
    PlayRouletteFragment fragment;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fragment.titleTextView = mockTitleTextView;
    }

    @Test
    public void testSetTitle() {
        String expectedTitle = "Test Title";
        fragment.titleTextView.setText(expectedTitle);
        verify(mockTitleTextView).setText(expectedTitle);
    }
}